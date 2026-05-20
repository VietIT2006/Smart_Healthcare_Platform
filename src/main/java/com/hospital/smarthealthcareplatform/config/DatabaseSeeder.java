package com.hospital.smarthealthcareplatform.config;

import com.hospital.smarthealthcareplatform.entity.Specialty;
import com.hospital.smarthealthcareplatform.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Override
    public void run(String... args) throws Exception {
        // Nếu bảng chưa có dữ liệu thì tự động bơm vào (Seed data)
        if (specialtyRepository.count() == 0) {
            System.out.println("==> Đang khởi tạo dữ liệu Chuyên khoa nền tảng (CORE-04)...");

            Specialty s1 = new Specialty();
            s1.setName("Nội Tổng Quát");
            s1.setDescription("Khám và chẩn đoán các bệnh lý nội khoa chung");

            Specialty s2 = new Specialty();
            s2.setName("Nhi Khoa");
            s2.setDescription("Khám và điều trị bệnh cho trẻ em");

            Specialty s3 = new Specialty();
            s3.setName("Tai Mũi Họng");
            s3.setDescription("Khám chuyên sâu các bệnh lý vùng đầu cổ");

            specialtyRepository.save(s1);
            specialtyRepository.save(s2);
            specialtyRepository.save(s3);

            System.out.println("==> Đã bơm xong dữ liệu Chuyên khoa!");
        }
    }
}