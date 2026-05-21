package com.hospital.smarthealthcareplatform.controller.doctor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.smarthealthcareplatform.entity.Appointment;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.repository.AppointmentRepository;
import com.hospital.smarthealthcareplatform.repository.MedicineRepository;
import com.hospital.smarthealthcareplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctor/appointments")
public class DoctorAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. API: Lấy danh sách bệnh nhân đang chờ khám (PENDING)
    @GetMapping("/queue")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getQueue(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findById(userId).orElseThrow();
        Long doctorId = user.getDoctorProfile().getId();

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndStatusOrderByAppointmentDateAscAppointmentTimeAsc(doctorId, "PENDING");

        List<Map<String, Object>> response = new ArrayList<>();
        for (Appointment a : appointments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("patientName", a.getPatient().getUserProfile().getFullName());
            map.put("date", a.getAppointmentDate());
            map.put("time", a.getAppointmentTime());
            map.put("symptoms", a.getSymptoms());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
    // 2. API: Cấp danh sách thuốc riêng cho Bác sĩ (Vượt trạm gác Admin)
    @GetMapping("/medicines")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getDoctorMedicines() {
        return ResponseEntity.ok(medicineRepository.findAll());
    }

    // 3. API: Hoàn thành ca khám & CHUYỂN ĐƠN THUỐC XUỐNG QUẦY (CORE-08)
    @PostMapping("/complete")
    @Transactional
    public ResponseEntity<?> completeExam(@RequestBody Map<String, Object> payload) {
        try {
            Long appId = Long.valueOf(payload.get("appointmentId").toString());
            String diagnosis = payload.get("diagnosis").toString();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> prescriptions = (List<Map<String, Object>>) payload.get("prescriptions");
            Appointment app = appointmentRepository.findById(appId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ca khám"));
            app.setStatus("COMPLETED");
            app.setDiagnosis(diagnosis);

            // LOGIC MỚI: Chỉ lưu đơn thuốc vào "túi", KHÔNG tự ý trừ kho
            if (prescriptions != null && !prescriptions.isEmpty()) {
                // Biến mảng thuốc thành chuỗi JSON để lưu xuống Database
                app.setPrescriptionDetails(objectMapper.writeValueAsString(prescriptions));
                // Bật cờ "Chờ cấp phát" để Admin nhận diện
                app.setDispenseStatus("PENDING");
            } else {
                app.setDispenseStatus("NONE");
            }

            appointmentRepository.save(app);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}