package com.hospital.smarthealthcareplatform.dto.response;

import lombok.Data;

@Data
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String qualification; // Bằng cấp (VD: Tiến sĩ Y Khoa)
}