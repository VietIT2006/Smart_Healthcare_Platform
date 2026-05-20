package com.hospital.smarthealthcareplatform.controller.patient;

import com.hospital.smarthealthcareplatform.dto.response.DoctorResponse;
import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
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

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponse>> getDoctorsBySpecialty(@RequestParam Long specialtyId) {
        // Lấy danh sách thực thể Doctor từ Database
        List<Doctor> doctors = doctorRepository.findBySpecialtyId(specialtyId);

        // Chuyển đổi sang dạng DTO để gửi về Giao diện
        List<DoctorResponse> responseList = new ArrayList<>();

        for (Doctor d : doctors) {
            DoctorResponse dto = new DoctorResponse();
            dto.setId(d.getId());
            dto.setQualification(d.getQualification());

            // Lấy Tên hiển thị từ bảng UserProfile thông qua mối quan hệ
            if (d.getUser() != null && d.getUser().getUserProfile() != null) {
                dto.setFullName(d.getUser().getUserProfile().getFullName());
            } else {
                dto.setFullName("Bác sĩ chưa cập nhật tên");
            }

            responseList.add(dto);
        }

        return ResponseEntity.ok(responseList);
    }
}