package com.hospital.smarthealthcareplatform.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileResponse {
    private String username;
    private String role;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;

    private String clinicRoom;
    private String biography;
    private Double consultationFee;
    private Integer experienceYears;
    private String qualification;
    private String specialtyName;
}