package com.hospital.smarthealthcareplatform.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/login"})
    public String loginPage(HttpSession session) {
        if (session.getAttribute("userRole") != null) {
            return "redirect:/profile";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session) {
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        return "profile";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {
        return "admin-medicines";
    }
}