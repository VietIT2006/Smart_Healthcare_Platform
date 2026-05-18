package com.hospital.smarthealthcareplatform.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Băm mật khẩu (CORE-01)
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Kiểm tra mật khẩu khi đăng nhập
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}