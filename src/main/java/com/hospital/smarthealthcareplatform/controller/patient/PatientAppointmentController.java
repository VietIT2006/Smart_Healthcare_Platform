package com.hospital.smarthealthcareplatform.controller.patient;

import com.hospital.smarthealthcareplatform.dto.request.AppointmentRequest;
import com.hospital.smarthealthcareplatform.service.impl.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request, HttpSession session) {
        // Lấy mã ID của bệnh nhân đang thao tác trong session plano
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Phiên làm việc hết hạn!"));
        }

        try {
            appointmentService.bookAppointment(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "Đã ghi nhận đặt lịch khám bệnh thành công!"));
        } catch (RuntimeException e) {
            // Ném HTTP 409 Conflict kèm nội dung thông báo trùng lịch ra màn hình
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}