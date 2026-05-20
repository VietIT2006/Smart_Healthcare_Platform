package com.hospital.smarthealthcareplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate; // Ngày khám

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime; // Giờ khám (VD: 08:00, 08:30)

    @Column(length = 20, nullable = false)
    private String status = "PENDING"; // Các trạng thái: PENDING (Chờ khám), CONFIRMED (Đã xác nhận), COMPLETED (Đã khám xong), CANCELLED (Đã hủy)

    @Column(columnDefinition = "TEXT")
    private String symptoms; // Triệu chứng bệnh nhân tự điền

    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú thêm

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian đặt lịch

    // Liên kết N-1 với Bác sĩ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Liên kết N-1 với Bệnh nhân (Từ bảng users)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
}