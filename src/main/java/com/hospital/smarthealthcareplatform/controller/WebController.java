package com.hospital.smarthealthcareplatform.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/login"})
    public String loginPage(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (role != null) {
            if ("ADMIN".equals(role)) return "redirect:/admin/dashboard";
            if ("DOCTOR".equals(role)) return "redirect:/doctor/dashboard";
            if ("PATIENT".equals(role)) return "redirect:/patient/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Giao diện Hồ sơ cá nhân dùng chung thích ứng theo Role (CORE-03)
    @GetMapping("/profile")
    public String profilePage(HttpSession session) {
        // Bảo vệ trạm gác: Nếu chưa đăng nhập thì bắt buộc quay về trang login
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "profile"; // Tìm và mở file src/main/resources/templates/profile.html
    }

    // Giao diện Dashboard cho Bệnh nhân (CORE-03 & CORE-05)
    @GetMapping("/patient/dashboard")
    public String patientDashboard(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (role == null || !"PATIENT".equals(role)) {
            return "redirect:/login";
        }
        return "patient-dashboard"; // Mở file templates/patient-dashboard.html
    }

    // Giao diện Dashboard cho Bác sĩ (CORE-06)
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (role == null || !"DOCTOR".equals(role)) {
            return "redirect:/login";
        }
        return "doctor-dashboard"; // Mở file templates/doctor-dashboard.html
    }

    // Giao diện Dashboard cho Admin quản lý thuốc (CORE-04)
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (role == null || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "admin-dashboard";
    }
}