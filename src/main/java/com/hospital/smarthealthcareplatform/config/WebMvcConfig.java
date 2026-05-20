package com.hospital.smarthealthcareplatform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RoleSecurityInterceptor roleSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng bộ lọc cho toàn bộ hệ thống, trừ các endpoint đăng ký/đăng nhập công khai
        registry.addInterceptor(roleSecurityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/v1/auth/**",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}