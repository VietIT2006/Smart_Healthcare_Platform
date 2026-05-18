package com.hospital.smarthealthcareplatform.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Tên đăng nhập không được trống")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String password;

    @NotBlank(message = "Quyền tài khoản không được trống")
    private String role; // PATIENT, DOCTOR, hoặc ADMIN
}
