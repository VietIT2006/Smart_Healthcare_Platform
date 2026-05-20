package com.hospital.smarthealthcareplatform.controller.user;

import com.hospital.smarthealthcareplatform.dto.request.ProfileUpdateRequest;
import com.hospital.smarthealthcareplatform.dto.response.UserProfileResponse;
import com.hospital.smarthealthcareplatform.entity.User;
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

    // Xem Hồ Sơ Cá Nhân công bằng cho mọi Role (CORE-03)
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Hết phiên làm việc. Vui lòng đăng nhập lại!"));
        }

        UserProfile profile = userService.getProfile(currentUser.getId());
        UserProfileResponse responseDTO = convertToResponseDTO(currentUser, profile);

        return ResponseEntity.ok(responseDTO);
    }

    // Cập Nhật Hồ Sơ Cá Nhân (CORE-03)
    @PutMapping("/profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody ProfileUpdateRequest request, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Hết phiên làm việc. Vui lòng đăng nhập lại!"));
        }

        UserProfile updatedProfile = userService.updateProfile(currentUser.getId(), request);
        UserProfileResponse responseDTO = convertToResponseDTO(currentUser, updatedProfile);

        return ResponseEntity.ok(responseDTO);
    }

    // Hàm ánh xạ Entity sang DTO an toàn
    private UserProfileResponse convertToResponseDTO(User user, UserProfile profile) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        if (profile != null) {
            dto.setFullName(profile.getFullName());
            dto.setPhone(profile.getPhone());
            dto.setGender(profile.getGender());
            dto.setDob(profile.getDob());
            dto.setAddress(profile.getAddress());
        }
        return dto;
    }
}