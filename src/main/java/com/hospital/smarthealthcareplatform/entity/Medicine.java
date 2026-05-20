package com.hospital.smarthealthcareplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "medicines")
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_name", nullable = false, unique = true, length = 100)
    private String medicineName;

    @Column(length = 50)
    private String category; // Nhóm thuốc (Kháng sinh, Giảm đau...)

    @Column(length = 20)
    private String unit; // Đơn vị tính (Viên, Hộp, Vỉ, Chai...)

    @Column(nullable = false)
    private Double price; // Đơn giá

    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock = 0; // Tồn kho

    @Column(name = "is_active")
    private boolean active = true; // Hỗ trợ Xóa mềm (Soft delete)
}