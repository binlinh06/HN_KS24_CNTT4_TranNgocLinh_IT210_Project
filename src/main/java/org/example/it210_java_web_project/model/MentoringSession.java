package org.example.it210_java_web_project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentoring_sessions")
@Getter
@Setter
public class MentoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= STUDENT =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // ================= LECTURER =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;

    // ================= TIME =================
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // ================= NOTE (GHI CHÚ) =================
    // ĐÃ THÊM TRƯỜNG NÀY ĐỂ HỨNG DỮ LIỆU TỪ FORM HTML
    @Column(columnDefinition = "TEXT")
    private String note;

    // ================= STATUS =================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;

    // ================= CREATED AT =================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ================= EVALUATION =================
    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private AcademicEvaluation evaluation;

    // ================= BORROWING =================
    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private BorrowingRecord borrowingRecord;

    // ================= AUTO CREATE TIME =================
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}