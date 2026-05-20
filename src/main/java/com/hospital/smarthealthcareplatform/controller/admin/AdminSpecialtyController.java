package com.hospital.smarthealthcareplatform.controller.admin;

import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.entity.Specialty;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
import com.hospital.smarthealthcareplatform.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminSpecialtyController {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // 1. Lấy danh sách tất cả chuyên khoa hiện có
    @GetMapping("/specialties")
    public ResponseEntity<List<Specialty>> getAllSpecialties() {
        return ResponseEntity.ok(specialtyRepository.findAll());
    }

    // 2. Thêm chuyên khoa mới trực tiếp từ giao diện Admin
    @PostMapping("/specialties")
    public ResponseEntity<?> createSpecialty(@RequestBody Specialty specialty) {
        if (specialty.getName() == null || specialty.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chuyên khoa không được để trống!");
        }
        return ResponseEntity.ok(specialtyRepository.save(specialty));
    }

    // 3. Lấy danh sách Bác sĩ demo trong hệ thống để phục vụ gán khoa
    // 3. Lấy danh sách Bác sĩ demo trong hệ thống để phục vụ gán khoa
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Doctor d : doctors) {
            Map<String, Object> map = new HashMap<>();
            // Sửa toàn bộ set() thành put()
            map.put("id", d.getId());
            map.put("clinicRoom", d.getClinicRoom() != null ? d.getClinicRoom() : "Chưa xếp");
            map.put("qualification", d.getQualification() != null ? d.getQualification() : "Chưa cập nhật");
            map.put("specialtyName", d.getSpecialty() != null ? d.getSpecialty().getName() : "Chưa gán khoa");

            if (d.getUser() != null && d.getUser().getUserProfile() != null) {
                map.put("fullName", d.getUser().getUserProfile().getFullName());
            } else {
                map.put("fullName", "Bác sĩ ẩn danh");
            }
            resultList.add(map);
        }
        return ResponseEntity.ok(resultList);
    }

    // 4. API gán hoặc thay đổi chuyên khoa cho một Bác sĩ cụ thể
    @PutMapping("/doctors/{id}/specialty")
    public ResponseEntity<?> assignSpecialtyToDoctor(@PathVariable Long id, @RequestParam Long specialtyId) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin Bác sĩ"));
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chuyên khoa tương ứng"));

        doctor.setSpecialty(specialty);
        doctorRepository.save(doctor);
        return ResponseEntity.ok(Map.of("message", "Đã điều chuyển chuyên khoa cho Bác sĩ thành công!"));
    }
}