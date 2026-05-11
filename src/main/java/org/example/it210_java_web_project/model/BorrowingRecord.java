package org.example.it210_java_web_project.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "borrowing_records")
@Getter @Setter
public class BorrowingRecord {

    @Id
    private Long sessionId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "session_id")
    private MentoringSession session;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @Enumerated(EnumType.STRING)
    private BorrowStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<BorrowingDetail> details;
}
