package com.hospital.smarthealthcareplatform.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);

        // Đọc dữ liệu dạng String an toàn tuyệt đối
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;

        System.out.println("==> TRẠM GÁC CORE-02: Đang kiểm tra Path: " + path);
        if (username != null) {
            System.out.println("==> Người dùng: " + username + " | Quyền: " + userRole);
        } else {
            System.out.println("==> Người dùng: Khách vãng lai");
        }

        // 1. CHẶN PHÂN VÙNG BÁC SĨ
        if (path.startsWith("/api/v1/doctor") || path.startsWith("/doctor")) {
            if (userRole == null) {
                sendCustomError(response, HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập! Vui lòng đăng nhập tài khoản Bác sĩ.");
                return false;
            }
            if (!"DOCTOR".equals(userRole)) {
                sendCustomError(response, HttpServletResponse.SC_FORBIDDEN, "Từ chối truy cập: Tài khoản của bạn không phải là Bác sĩ!");
                return false;
            }
        }

        // 2. CHẶN PHÂN VÙNG ADMIN
        if (path.startsWith("/api/v1/admin") || path.startsWith("/admin")) {
            if (userRole == null) {
                sendCustomError(response, HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập! Vui lòng đăng nhập tài khoản Admin.");
                return false;
            }
            if (!"ADMIN".equals(userRole)) {
                sendCustomError(response, HttpServletResponse.SC_FORBIDDEN, "Từ chối truy cập: Khu vực quản trị tối cao chỉ dành cho Admin!");
                return false;
            }
        }

        return true;
    }

    private void sendCustomError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\": " + status + ", \"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}