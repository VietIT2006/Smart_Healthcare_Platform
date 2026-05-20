// MedicineRepository.java
package com.hospital.smarthealthcareplatform.repository;
import com.hospital.smarthealthcareplatform.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByActiveTrue();
    boolean existsByMedicineName(String medicineName);
}