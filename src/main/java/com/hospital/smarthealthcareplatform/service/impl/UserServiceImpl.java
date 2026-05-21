package com.hospital.smarthealthcareplatform.service.impl;

import com.hospital.smarthealthcareplatform.dto.request.ProfileUpdateRequest;
import com.hospital.smarthealthcareplatform.dto.request.RegisterRequest;
import com.hospital.smarthealthcareplatform.dto.response.UserProfileResponse;
import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.entity.UserProfile;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
import com.hospital.smarthealthcareplatform.repository.UserRepository;
import com.hospital.smarthealthcareplatform.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên tài khoản này đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.hashPassword(request.getPassword()));
        user.setRole(request.getRole().toUpperCase());

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName("Chưa cập nhật tên");
        user.setUserProfile(profile);

        User savedUser = userRepository.save(user);

        // Logic tự động sinh Hồ sơ Bác sĩ chờ duyệt
        if ("DOCTOR".equals(savedUser.getRole())) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            doctorRepository.save(doctor);
        }

        return savedUser;
    }

    public User login(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không đúng"));

        if (!PasswordUtils.checkPassword(plainPassword, user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không đúng");
        }
        return user;
    }

    // 🚀 ĐÂY CHÍNH LÀ HÀM MÀ TRÌNH BIÊN DỊCH ĐANG BÁO THIẾU
    public UserProfileResponse getUserProfileDto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        UserProfile profile = user.getUserProfile();
        UserProfileResponse dto = new UserProfileResponse();

        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());

        if (profile != null) {
            dto.setFullName(profile.getFullName());
            dto.setPhone(profile.getPhone());
            dto.setGender(profile.getGender());
            dto.setDob(profile.getDob());
            dto.setAddress(profile.getAddress());
        }

        // Tự động nạp thêm dữ liệu lâm sàng nếu là Bác sĩ
        if ("DOCTOR".equals(user.getRole()) && user.getDoctorProfile() != null) {
            Doctor doc = user.getDoctorProfile();
            dto.setClinicRoom(doc.getClinicRoom());
            dto.setBiography(doc.getBiography());
            dto.setConsultationFee(doc.getConsultationFee());
            dto.setExperienceYears(doc.getExperienceYears());
            dto.setQualification(doc.getQualification());
            if (doc.getSpecialty() != null) {
                dto.setSpecialtyName(doc.getSpecialty().getName());
            }
        }

        return dto;
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

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

        // Lưu thông tin y khoa song song nếu là Bác sĩ
        if ("DOCTOR".equals(user.getRole()) && user.getDoctorProfile() != null) {
            Doctor doc = user.getDoctorProfile();
            doc.setClinicRoom(request.getClinicRoom());
            doc.setBiography(request.getBiography());
            doc.setConsultationFee(request.getConsultationFee());
            doc.setExperienceYears(request.getExperienceYears());
            doc.setQualification(request.getQualification());
            doctorRepository.save(doc);
        }

        return getUserProfileDto(userId);
    }
}