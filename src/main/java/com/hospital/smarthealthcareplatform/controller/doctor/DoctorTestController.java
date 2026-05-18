package com.hospital.smarthealthcareplatform.controller.doctor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorTestController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDoctorDashboard() {
        return ResponseEntity.ok("Chào mừng Bác sĩ! Đây là dữ liệu nội bộ của phân khoa khám bệnh.");
    }
}
