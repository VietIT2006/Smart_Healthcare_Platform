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
    private LocalTime appointmentTime; // Giờ khám

    @Column(length = 20, nullable = false)
    private String status = "PENDING";

    @Column(columnDefinition = "TEXT")
    private String symptoms; // Triệu chứng bệnh nhân tự điền

    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú thêm

    // 🚀 THÊM TRƯỜNG NÀY ĐỂ LƯU CHẨN ĐOÁN CỦA BÁC SĨ (CORE-06)
    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Liên kết N-1 với Bác sĩ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Liên kết N-1 với Bệnh nhân
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
}