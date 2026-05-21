package com.hospital.smarthealthcareplatform.controller.user;

import com.hospital.smarthealthcareplatform.dto.request.ProfileUpdateRequest;
import com.hospital.smarthealthcareplatform.dto.response.UserProfileResponse;
import com.hospital.smarthealthcareplatform.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class ProfileController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Vui lòng đăng nhập!"));
        }
        UserProfileResponse profileDto = userService.getUserProfileDto(userId);
        return ResponseEntity.ok(profileDto);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Phiên đăng nhập hết hạn!"));
        }
        UserProfileResponse updated = userService.updateProfile(userId, request);
        return ResponseEntity.ok(updated);
    }
}