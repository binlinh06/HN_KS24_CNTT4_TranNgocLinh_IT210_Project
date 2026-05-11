package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.Appointment;
import org.example.it210_java_web_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Kiểm tra duplicate cùng giảng viên + cùng giờ
    boolean existsByLecturerAndAppointmentTime(
            User lecturer,
            LocalDateTime appointmentTime
    );
}