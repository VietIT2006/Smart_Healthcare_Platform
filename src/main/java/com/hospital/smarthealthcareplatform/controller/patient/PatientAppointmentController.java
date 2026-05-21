package com.hospital.smarthealthcareplatform.controller.patient;

import com.hospital.smarthealthcareplatform.entity.Appointment;
import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.repository.AppointmentRepository;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
import com.hospital.smarthealthcareplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patient/appointments")
public class PatientAppointmentController {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DoctorRepository doctorRepository;

    // 1. API: XỬ LÝ ĐẶT LỊCH KHÁM
    @PostMapping
    @Transactional
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> payload, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phiên đăng nhập hết hạn!");

        try {
            User patient = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu bệnh nhân"));

            Long doctorId = Long.valueOf(payload.get("doctorId").toString());
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin Bác sĩ"));

            LocalDate date = LocalDate.parse(payload.get("appointmentDate").toString());
            LocalTime time = LocalTime.parse(payload.get("appointmentTime").toString());
            String symptoms = payload.get("symptoms").toString();

            // Logic chặn trùng lịch: 1 Bác sĩ không thể khám 2 bệnh nhân cùng 1 khung giờ
            boolean isConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                    doctorId, date, time, "CANCELED");

            if (isConflict) {
                return ResponseEntity.badRequest().body("Bác sĩ đã có lịch hẹn vào khung giờ này. Vui lòng chọn khung giờ khác!");
            }

            // Lưu bệnh án
            Appointment app = new Appointment();
            app.setPatient(patient);
            app.setDoctor(doctor);
            app.setAppointmentDate(date);
            app.setAppointmentTime(time);
            app.setSymptoms(symptoms);
            app.setStatus("PENDING");
            app.setDispenseStatus("NONE");

            appointmentRepository.save(app);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. CORE-07: Tra cứu Hồ sơ Y tế Liên kết (Lịch sử)
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Appointment> appointments = appointmentRepository
                .findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(userId);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Appointment a : appointments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("doctorName", a.getDoctor().getUser().getUserProfile().getFullName());
            map.put("specialty", a.getDoctor().getSpecialty() != null ? a.getDoctor().getSpecialty().getName() : "Khám tổng quát");
            map.put("date", a.getAppointmentDate());
            map.put("time", a.getAppointmentTime());
            map.put("status", a.getStatus());
            map.put("diagnosis", a.getDiagnosis());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // 3. CORE-09: Bệnh nhân chủ động hủy lịch
    @PutMapping("/cancel/{id}")
    @Transactional
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Appointment app = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));

            if (!app.getPatient().getId().equals(userId)) {
                throw new RuntimeException("Bạn không có quyền thực hiện hành động này!");
            }
            if (!"PENDING".equals(app.getStatus())) {
                throw new RuntimeException("Chỉ có thể hủy lịch đang chờ khám!");
            }

            LocalDateTime appointmentDateTime = LocalDateTime.of(app.getAppointmentDate(), app.getAppointmentTime());
            LocalDateTime now = LocalDateTime.now();

            long hoursBetween = ChronoUnit.HOURS.between(now, appointmentDateTime);
            if (hoursBetween < 24) {
                throw new RuntimeException("Hủy lịch thất bại! Bạn chỉ được phép hủy trước giờ khám ít nhất 24 tiếng.");
            }

            app.setStatus("CANCELED");
            appointmentRepository.save(app);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}