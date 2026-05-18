package com.hospital.smarthealthcareplatform.service.impl;

import com.hospital.smarthealthcareplatform.dto.request.RegisterRequest;
import com.hospital.smarthealthcareplatform.entity.User;
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

        // TIẾN HÀNH BĂM MẬT KHẨU TRƯỚC KHI LƯU
        String hashed = PasswordUtils.hashPassword(request.getPassword());
        user.setPassword(hashed);

        user.setRole(request.getRole().toUpperCase());

        return userRepository.save(user);
    }

    public User login(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không đúng"));

        // KIỂM TRA MẬT KHẨU THÔ VỚI CHUỖI BĂM TRONG DB
        if (!PasswordUtils.checkPassword(plainPassword, user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không đúng");
        }

        return user;
    }
}