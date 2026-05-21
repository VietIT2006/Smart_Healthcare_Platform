package com.hospital.smarthealthcareplatform.controller.admin;

import com.hospital.smarthealthcareplatform.entity.Appointment;
import com.hospital.smarthealthcareplatform.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/appointments")
public class AdminAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        for (Appointment a : appointments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("patientName", a.getPatient().getUserProfile().getFullName());
            map.put("doctorName", a.getDoctor().getUser().getUserProfile().getFullName());
            map.put("date", a.getAppointmentDate());
            map.put("time", a.getAppointmentTime());
            map.put("status", a.getStatus());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}