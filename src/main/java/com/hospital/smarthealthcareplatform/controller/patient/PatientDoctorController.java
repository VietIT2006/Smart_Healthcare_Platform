package com.hospital.smarthealthcareplatform.controller.patient;

import com.hospital.smarthealthcareplatform.dto.response.DoctorResponse;
import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
import com.hospital.smarthealthcareplatform.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientDoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // Kéo thêm Repository của Chuyên khoa vào
    @Autowired
    private SpecialtyRepository specialtyRepository;

    // 1. API: Lấy danh sách chuyên khoa tự động từ Database
    @GetMapping("/specialties")
    public ResponseEntity<?> getAllSpecialties() {
        return ResponseEntity.ok(specialtyRepository.findAll());
    }

    // 2. API: Lấy bác sĩ theo ID chuyên khoa
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponse>> getDoctorsBySpecialty(@RequestParam Long specialtyId) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyId(specialtyId);
        List<DoctorResponse> responseList = new ArrayList<>();

        for (Doctor d : doctors) {
            DoctorResponse dto = new DoctorResponse();
            dto.setId(d.getId());

            // Fix lỗi hiển thị chữ "null" nếu bác sĩ chưa cập nhật học vị
            dto.setQualification(d.getQualification() != null ? d.getQualification() : "Chưa cập nhật");

            if (d.getUser() != null && d.getUser().getUserProfile() != null) {
                dto.setFullName(d.getUser().getUserProfile().getFullName());
            } else {
                dto.setFullName("Bác sĩ ẩn danh");
            }

            responseList.add(dto);
        }

        return ResponseEntity.ok(responseList);
    }
}