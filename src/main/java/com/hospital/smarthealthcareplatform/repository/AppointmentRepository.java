package com.hospital.smarthealthcareplatform.repository;

import com.hospital.smarthealthcareplatform.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Logic chống xung đột (CORE-05): Kiểm tra xem Bác sĩ tại ngày và giờ đó
    // đã có lịch hẹn nào chưa (ngoại trừ các lịch đã bị CANCELLED)
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String status);
}