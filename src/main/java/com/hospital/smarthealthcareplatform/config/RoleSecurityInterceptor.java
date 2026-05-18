package com.hospital.smarthealthcareplatform.config;

import com.hospital.smarthealthcareplatform.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Sử dụng getServletPath() để loại bỏ hoàn toàn Context Path, chỉ lấy đúng API Route
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);

        // Lấy User từ Session
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Dòng In kiểm tra trực tiếp trên Console của IntelliJ để nghiệm thu
        System.out.println("==> TRẠM GÁC CORE-02: Đang kiểm tra Path: " + path);
        if (user != null) {
            System.out.println("==> Người dùng hiện tại: " + user.getUsername() + " | Quyền: " + user.getRole());
        } else {
            System.out.println("==> Người dùng hiện tại: Khách vãng lai (Chưa đăng nhập)");
        }

        // 1. CHẶN PHÂN VÙNG BÁC SĨ
        if (path.startsWith("/api/v1/doctor") || path.startsWith("/doctor")) {
            if (user == null) {
                sendCustomError(response, HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập! Vui lòng đăng nhập tài khoản Bác sĩ.");
                return false;
            }
            if (!"DOCTOR".equals(user.getRole().toUpperCase())) {
                sendCustomError(response, HttpServletResponse.SC_FORBIDDEN, "Từ chối truy cập: Tài khoản của bạn không phải là Bác sĩ!");
                return false;
            }
        }

        // 2. CHẶN PHÂN VÙNG ADMIN
        if (path.startsWith("/api/v1/admin") || path.startsWith("/admin")) {
            if (user == null) {
                sendCustomError(response, HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập! Vui lòng đăng nhập tài khoản Admin.");
                return false;
            }
            if (!"ADMIN".equals(user.getRole().toUpperCase())) {
                sendCustomError(response, HttpServletResponse.SC_FORBIDDEN, "Từ chối truy cập: Khu vực quản trị tối cao chỉ dành cho Admin!");
                return false;
            }
        }

        return true; // Hợp lệ, cho phép đi tiếp vào Controller
    }

    // Hàm tiện ích hỗ trợ xuất thông báo lỗi tiếng Việt dạng JSON ra màn hình Postman công bằng
    private void sendCustomError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\": " + status + ", \"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}