package org.example.it210_java_web_project.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "academic_evaluations")
@Getter @Setter
public class AcademicEvaluation {

    @Id
    private Long sessionId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "session_id")
    private MentoringSession session;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private User lecturer;

    private String comment;

    private Integer rating;

    private LocalDateTime createdAt;
}
