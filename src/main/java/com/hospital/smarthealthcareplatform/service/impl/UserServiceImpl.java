package com.hospital.smarthealthcareplatform.service.impl;

import com.hospital.smarthealthcareplatform.dto.request.ProfileUpdateRequest;
import com.hospital.smarthealthcareplatform.dto.request.RegisterRequest;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.entity.UserProfile;
import com.hospital.smarthealthcareplatform.repository.UserRepository;
import com.hospital.smarthealthcareplatform.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên tài khoản này đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(request.getUsername());

        String hashed = PasswordUtils.hashPassword(request.getPassword());
        user.setPassword(hashed);

        user.setRole(request.getRole().toUpperCase());

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName("Chưa cập nhật tên"); // Giá trị định danh ban đầu
        user.setUserProfile(profile);

        return userRepository.save(user);
    }

    public User login(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không đúng"));

        if (!PasswordUtils.checkPassword(plainPassword, user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không đúng");
        }

        return user;
    }

    public UserProfile getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hệ thống"));
        return user.getUserProfile();
    }

    @Transactional
    public UserProfile updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hệ thống"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setGender(request.getGender());
        profile.setDob(request.getDob());
        profile.setAddress(request.getAddress());

        user.setUserProfile(profile);
        userRepository.save(user);
        return profile;
    }
}