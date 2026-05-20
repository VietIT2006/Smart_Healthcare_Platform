package com.hospital.smarthealthcareplatform.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // 1. Điều hướng trang chủ hoặc trang Đăng nhập
    @GetMapping({"/", "/login"})
    public String loginPage(HttpSession session) {
        // Cơ chế bọc đầu: Nếu đã có session currentUser, tự động đá sang trang profile
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/profile";
        }
        return "login"; // Tìm file src/main/resources/templates/login.html
    }

    // 2. Điều hướng trang Đăng ký tài khoản
    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Tìm file src/main/resources/templates/register.html
    }

    // 3. Điều hướng trang Hồ sơ người dùng (Bảo vệ tầng View)
    @GetMapping("/profile")
    public String profilePage(HttpSession session) {
        // Chặn trực tiếp từ tầng điều hướng: Chưa đăng nhập không cho xem trang, đuổi về login
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }
        return "profile"; // Tìm file src/main/resources/templates/profile.html
    }
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {
        // Note: Bộ lọc Interceptor đã lo phần bảo mật role ADMIN
        return "admin-medicines";
    }
}