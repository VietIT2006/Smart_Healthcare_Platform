package com.hospital.smarthealthcareplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 100)
    private String qualification; // Bằng cấp (VD: Thạc sĩ, Tiến sĩ...)

    @Column(name = "consultation_fee")
    private Double consultationFee; // Phí khám bệnh

    // Liên kết 1-1 với tài khoản User hệ thống
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Liên kết N-1 với Chuyên khoa (Nhiều bác sĩ thuộc 1 chuyên khoa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    // Bổ sung thêm 2 trường này vào class Doctor
    @Column(name = "clinic_room", length = 50)
    private String clinicRoom;

    @Column(columnDefinition = "TEXT")
    private String biography;
}