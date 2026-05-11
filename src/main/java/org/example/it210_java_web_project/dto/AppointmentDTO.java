package org.example.it210_java_web_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {

    private Long lecturerId;

    private LocalDateTime appointmentTime;

    private String note;
}