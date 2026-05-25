package com.hospital.smarthealthcareplatform.controller.auth;

import com.hospital.smarthealthcareplatform.dto.request.LoginRequest;
import com.hospital.smarthealthcareplatform.dto.request.RegisterRequest;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        try {
            // Đưa logic đăng nhập vào khối try
            User user = userService.login(request.getUsername(), request.getPassword());

            HttpSession oldSession = servletRequest.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = servletRequest.getSession(true);
            // LƯU CÁC THUỘC TÍNH NGUYÊN THỦY (Tránh lỗi lệch ClassLoader của DevTools)
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("username", user.getUsername());
            newSession.setAttribute("userRole", user.getRole().toUpperCase());

            return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "role", user.getRole().toUpperCase(),
                    "message", "Đăng nhập thành công!"
            ));

        } catch (RuntimeException e) {
            // 🚀 KHI SAI MẬT KHẨU: Bắt lỗi ở đây và trả về JSON có chứa "error"
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Lấy session hiện tại (nếu có)
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Hủy toàn bộ dữ liệu phiên làm việc (Xóa userId, username, userRole)
            session.invalidate();
        }

        return ResponseEntity.ok(Map.of("message", "Đã đăng xuất tài khoản an toàn!"));
    }
}