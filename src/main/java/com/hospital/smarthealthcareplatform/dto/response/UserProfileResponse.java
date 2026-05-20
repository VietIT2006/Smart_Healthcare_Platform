package com.hospital.smarthealthcareplatform.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String role;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
}