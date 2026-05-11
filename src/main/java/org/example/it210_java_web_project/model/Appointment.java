package org.example.it210_java_web_project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentoring_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sinh viên đặt lịch
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    // Giảng viên được đặt
    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private User lecturer;

    private LocalDateTime appointmentTime;

    private String note;
}