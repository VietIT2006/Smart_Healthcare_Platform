package com.hospital.smarthealthcareplatform.repository;
import com.hospital.smarthealthcareplatform.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {}