package com.hospital.smarthealthcareplatform.repository;

import com.hospital.smarthealthcareplatform.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Lấy danh sách Bác sĩ theo ID Chuyên khoa
    List<Doctor> findBySpecialtyId(Long specialtyId);
}