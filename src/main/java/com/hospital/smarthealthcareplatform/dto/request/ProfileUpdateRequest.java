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

    private String clinicRoom;
    private String biography;
    private Double consultationFee;
    private Integer experienceYears;
    private String qualification;
}