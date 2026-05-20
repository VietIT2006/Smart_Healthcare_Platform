package com.hospital.smarthealthcareplatform.controller.admin;

import com.hospital.smarthealthcareplatform.entity.Medicine;
import com.hospital.smarthealthcareplatform.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/medicines")
public class AdminMedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    // LẤY DANH SÁCH THUỐC (Chỉ lấy thuốc active)
    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(medicineRepository.findByActiveTrue());
    }

    // THÊM THUỐC
    @PostMapping
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        if (medicineRepository.existsByMedicineName(medicine.getMedicineName())) {
            return ResponseEntity.badRequest().body("Lỗi: Thuốc này đã tồn tại!");
        }
        return ResponseEntity.ok(medicineRepository.save(medicine));
    }

    // SỬA THUỐC
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Long id, @RequestBody Medicine updatedData) {
        return medicineRepository.findById(id).map(med -> {
            med.setMedicineName(updatedData.getMedicineName());
            med.setCategory(updatedData.getCategory());
            med.setUnit(updatedData.getUnit());
            med.setPrice(updatedData.getPrice());
            med.setQuantityInStock(updatedData.getQuantityInStock());
            return ResponseEntity.ok(medicineRepository.save(med));
        }).orElse(ResponseEntity.notFound().build());
    }

    // XÓA THUỐC (Xóa mềm - Soft Delete)
    // Tại sao không xóa cứng? Vì nếu xóa cứng, các Đơn thuốc cũ ở CORE-07 sẽ bị lỗi mất dữ liệu!
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Long id) {
        return medicineRepository.findById(id).map(med -> {
            med.setActive(false);
            medicineRepository.save(med);
            return ResponseEntity.ok("Đã xóa thuốc thành công!");
        }).orElse(ResponseEntity.notFound().build());
    }
}