package com.hospital.smarthealthcareplatform.controller.user;

import com.hospital.smarthealthcareplatform.dto.request.ProfileUpdateRequest;
import com.hospital.smarthealthcareplatform.dto.response.UserProfileResponse;
import com.hospital.smarthealthcareplatform.entity.UserProfile;
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
    public ResponseEntity<?> getMyProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Hết phiên làm việc. Vui lòng đăng nhập lại!"));
        }

        UserProfile profile = userService.getProfile(userId);

        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(userId);
        dto.setUsername(username);
        dto.setRole(userRole);
        if (profile != null) {
            dto.setFullName(profile.getFullName());
            dto.setPhone(profile.getPhone());
            dto.setGender(profile.getGender());
            dto.setDob(profile.getDob());
            dto.setAddress(profile.getAddress());
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody ProfileUpdateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Hết phiên làm việc. Vui lòng đăng nhập lại!"));
        }

        UserProfile updatedProfile = userService.updateProfile(userId, request);

        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(userId);
        dto.setUsername(username);
        dto.setRole(userRole);
        if (updatedProfile != null) {
            dto.setFullName(updatedProfile.getFullName());
            dto.setPhone(updatedProfile.getPhone());
            dto.setGender(updatedProfile.getGender());
            dto.setDob(updatedProfile.getDob());
            dto.setAddress(updatedProfile.getAddress());
        }

        return ResponseEntity.ok(dto);
    }
}