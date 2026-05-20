package com.hospital.smarthealthcareplatform.service.impl;

import com.hospital.smarthealthcareplatform.dto.request.AppointmentRequest;
import com.hospital.smarthealthcareplatform.entity.Appointment;
import com.hospital.smarthealthcareplatform.entity.Doctor;
import com.hospital.smarthealthcareplatform.entity.User;
import com.hospital.smarthealthcareplatform.repository.AppointmentRepository;
import com.hospital.smarthealthcareplatform.repository.DoctorRepository;
import com.hospital.smarthealthcareplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public Appointment bookAppointment(Long patientId, AppointmentRequest request) {
        // 1. Kiểm tra chống trùng khung giờ bác sĩ trực tiếp
        boolean isConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                "CANCELLED"
        );

        if (isConflict) {
            throw new RuntimeException("Khung giờ này Bác sĩ đã có lịch hẹn tích hợp. Vui lòng chọn giờ khác!");
        }

        // 2. Định vị thông tin thực thể liên quan
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu Bệnh nhân"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu Bác sĩ"));

        // 3. Khởi tạo đối tượng lịch hẹn mới
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setSymptoms(request.getSymptoms());
        appointment.setStatus("PENDING"); // Mặc định ở trạng thái Chờ khám

        return appointmentRepository.save(appointment);
    }
}