package com.hospital.smarthealthcareplatform.controller.auth;

import com.hospital.smarthealthcareplatform.dto.request.LoginRequest;
import com.hospital.smarthealthcareplatform.dto.request.RegisterRequest;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = userService.register(request);
        return ResponseEntity.ok(Map.of(
                "status", "Thành công",
                "message", "Tài khoản " + savedUser.getUsername() + " đã được tạo bảo mật."
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        // 1. Gọi Service kiểm tra tài khoản, mật khẩu
        User user = userService.login(request.getUsername(), request.getPassword());

        // 2. SỬA LỖI TRỘN SESSION: Hủy session cũ nếu có tồn tại trước đó
        HttpSession oldSession = servletRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // 3. Tạo một Session mới tinh độc lập hoàn toàn
        HttpSession newSession = servletRequest.getSession(true);
        newSession.setAttribute("currentUser", user);

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole(),
                "message", "Đăng nhập thành công!"
        ));
    }
}
