package com.hospital.smarthealthcareplatform.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
}