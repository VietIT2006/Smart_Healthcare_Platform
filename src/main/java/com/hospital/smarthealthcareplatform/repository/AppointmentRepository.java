package com.hospital.smarthealthcareplatform.repository;

import com.hospital.smarthealthcareplatform.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Logic chống xung đột giờ khám
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String status);

    // KÉO DỮ LIỆU CHO BÁC SĨ: Tìm lịch theo ID bác sĩ, lọc trạng thái (PENDING) và xếp theo giờ
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAscAppointmentTimeAsc(Long doctorId, String status);

    // Lấy lịch sử khám của Bệnh nhân (Sắp xếp mới nhất lên đầu)
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);
}