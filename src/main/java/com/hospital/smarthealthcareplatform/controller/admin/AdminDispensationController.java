package com.hospital.smarthealthcareplatform.controller.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.smarthealthcareplatform.entity.Appointment;
import com.hospital.smarthealthcareplatform.entity.Medicine;
import com.hospital.smarthealthcareplatform.repository.AppointmentRepository;
import com.hospital.smarthealthcareplatform.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dispensations")
public class AdminDispensationController {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private MedicineRepository medicineRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. Load các Đơn thuốc đang vướng trạng thái PENDING
    @GetMapping("/queue")
    public ResponseEntity<?> getPendingDispensations() {
        List<Appointment> all = appointmentRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();
        for (Appointment a : all) {
            if ("PENDING".equals(a.getDispenseStatus())) {
                Map<String, Object> map = new HashMap<>();
                map.put("appointmentId", a.getId());
                map.put("patientName", a.getPatient().getUserProfile().getFullName());
                map.put("doctorName", a.getDoctor().getUser().getUserProfile().getFullName());
                try {
                    // Giải mã chuỗi JSON lấy lại danh sách thuốc để hiển thị cho Dược sĩ
                    List<Map<String, Object>> meds = objectMapper.readValue(a.getPrescriptionDetails(), new TypeReference<List<Map<String, Object>>>(){});
                    map.put("medicines", meds);
                } catch(Exception e) { map.put("medicines", new ArrayList<>()); }
                res.add(map);
            }
        }
        return ResponseEntity.ok(res);
    }

    // 2. Chốt hạ: Dược sĩ xác nhận phát thuốc ➔ LÚC NÀY MỚI TRỪ TỒN KHO
    @PostMapping("/confirm/{appId}")
    @Transactional
    public ResponseEntity<?> confirmDispense(@PathVariable Long appId) {
        try {
            Appointment app = appointmentRepository.findById(appId).orElseThrow();
            if (!"PENDING".equals(app.getDispenseStatus())) throw new RuntimeException("Đơn thuốc không hợp lệ!");

            List<Map<String, Object>> meds = objectMapper.readValue(app.getPrescriptionDetails(), new TypeReference<List<Map<String, Object>>>(){});
            for (Map<String, Object> m : meds) {
                Long medId = Long.valueOf(m.get("medicineId").toString());
                Integer qty = Integer.valueOf(m.get("quantity").toString());

                Medicine medicine = medicineRepository.findById(medId).orElseThrow();
                if (medicine.getQuantityInStock() < qty) {
                    throw new RuntimeException("Thuốc " + medicine.getMedicineName() + " đang hết hàng trong kho!");
                }
                // TRỪ KHO CHÍNH THỨC
                medicine.setQuantityInStock(medicine.getQuantityInStock() - qty);
                medicineRepository.save(medicine);
            }

            // Xóa cờ chờ cấp phát
            app.setDispenseStatus("COMPLETED");
            appointmentRepository.save(app);

            return ResponseEntity.ok("Success");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}