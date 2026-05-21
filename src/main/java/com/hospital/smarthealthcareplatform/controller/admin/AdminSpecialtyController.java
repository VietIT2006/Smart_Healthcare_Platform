package com.hospital.smarthealthcareplatform.controller.admin;

import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.entity.Specialty;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
import com.hospital.smarthealthcareplatform.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/specialties")
public class AdminSpecialtyController {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // 1. API: Admin tạo chuyên khoa mới
    @PostMapping
    @Transactional
    public ResponseEntity<?> createSpecialty(@RequestBody Specialty specialty) {
        specialtyRepository.save(specialty);
        return ResponseEntity.ok("Thêm chuyên khoa thành công");
    }

    // 2. API: Lấy danh sách TẤT CẢ Bác sĩ để Admin điều phối
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctorsForAdmin() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Doctor d : doctors) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());

            // Lấy tên thật của Bác sĩ từ UserProfile
            if (d.getUser() != null && d.getUser().getUserProfile() != null) {
                map.put("fullName", d.getUser().getUserProfile().getFullName());
            } else {
                map.put("fullName", "Bác sĩ ẩn danh");
            }

            map.put("qualification", d.getQualification());

            // Kiểm tra xem Bác sĩ đã được gán khoa nào chưa
            if (d.getSpecialty() != null) {
                map.put("specialtyName", d.getSpecialty().getName());
            } else {
                map.put("specialtyName", null);
            }

            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // 3. API: Admin gán/điều chuyển chuyên khoa cho Bác sĩ
    @PutMapping("/doctors/{doctorId}")
    @Transactional
    public ResponseEntity<?> assignSpecialtyToDoctor(@PathVariable Long doctorId, @RequestParam Long specialtyId) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ Bác sĩ"));

            Specialty specialty = specialtyRepository.findById(specialtyId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Chuyên khoa"));

            // Gán khóa ngoại specialty_id cho bác sĩ
            doctor.setSpecialty(specialty);
            doctorRepository.save(doctor);

            return ResponseEntity.ok("Điều chuyển khoa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}