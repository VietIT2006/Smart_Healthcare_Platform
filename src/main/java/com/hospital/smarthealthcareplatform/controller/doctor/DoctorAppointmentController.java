package com.hospital.smarthealthcareplatform.controller.doctor;

import com.hospital.smarthealthcareplatform.entity.Appointment;
import com.hospital.smarthealthcareplatform.entity.Medicine;
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

    // Kéo thêm Repository thuốc vào để thực hiện nghiệp vụ trừ kho
    @Autowired
    private MedicineRepository medicineRepository;

    // 1. API: Lấy danh sách bệnh nhân đang chờ khám
    @GetMapping("/queue")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getQueue(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findById(userId).orElseThrow();
        Long doctorId = user.getDoctorProfile().getId();

        // Lấy danh sách bệnh nhân đang CHỜ KHÁM (PENDING)
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

    // 2. API: Hoàn thành ca khám, lưu bệnh án và trừ tồn kho (CORE-06)
    @PostMapping("/complete")
    @Transactional // Đảm bảo tính toàn vẹn: Lỗi giữa chừng sẽ Rollback không trừ thuốc
    public ResponseEntity<?> completeExam(@RequestBody Map<String, Object> payload) {
        try {
            Long appId = Long.valueOf(payload.get("appointmentId").toString());
            String diagnosis = payload.get("diagnosis").toString();

            // Ép kiểu an toàn danh sách thuốc gửi lên từ giao diện
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> prescriptions = (List<Map<String, Object>>) payload.get("prescriptions");

            // Bước A: Cập nhật trạng thái bệnh án thành Đã khám (COMPLETED)
            Appointment app = appointmentRepository.findById(appId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ca khám"));
            app.setStatus("COMPLETED");
            app.setDiagnosis(diagnosis);
            appointmentRepository.save(app);

            // Bước B: Duyệt qua mảng thuốc bác sĩ kê và TRỪ TỒN KHO
            if (prescriptions != null) {
                for (Map<String, Object> p : prescriptions) {
                    Long medId = Long.valueOf(p.get("medicineId").toString());
                    Integer qty = Integer.valueOf(p.get("quantity").toString());

                    Medicine med = medicineRepository.findById(medId)
                            .orElseThrow(() -> new RuntimeException("Lỗi dữ liệu thuốc"));

                    // Chặn nếu bác sĩ kê vượt quá số thuốc đang có trong kho
                    if (med.getQuantityInStock() < qty) {
                        throw new RuntimeException("Thuốc " + med.getMedicineName() + " không đủ tồn kho!");
                    }

                    // Thực hiện trừ kho và lưu lại
                    med.setQuantityInStock(med.getQuantityInStock() - qty);
                    medicineRepository.save(med);
                }
            }
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            // Trả về lỗi cho giao diện hiển thị thông báo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}