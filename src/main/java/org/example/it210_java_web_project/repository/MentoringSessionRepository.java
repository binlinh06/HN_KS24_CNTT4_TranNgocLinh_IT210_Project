package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.MentoringSession;
import org.example.it210_java_web_project.model.SessionStatus;
import org.example.it210_java_web_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    // =========================================================
    // 1. LOGIC ĐẶT LỊCH: Kiểm tra Xung đột & Giải phóng Slot
    // =========================================================
    // Đã sửa: Dùng tham số :excludedStatuses thay vì hardcode Enum
    @Query("""
        SELECT COUNT(ms) > 0
        FROM MentoringSession ms
        WHERE ms.lecturer = :lecturer
        AND ms.status NOT IN :excludedStatuses
        AND (
            :startTime < ms.endTime
            AND
            :endTime > ms.startTime
        )
    """)
    boolean existsConflictSchedule(
            @Param("lecturer") User lecturer,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludedStatuses") List<SessionStatus> excludedStatuses
    );

    // =========================================================
    // 2. LOGIC DASHBOARD: Hiển thị lịch sắp tới cho Sinh viên
    // =========================================================
    // Đã sửa: Dùng tham số :excludedStatuses thay vì hardcode Enum
    @Query("""
        SELECT ms FROM MentoringSession ms 
        WHERE ms.student = :student 
        AND ms.status NOT IN :excludedStatuses 
        ORDER BY ms.startTime ASC
    """)
    List<MentoringSession> findUpcomingSessions(
            @Param("student") User student,
            @Param("excludedStatuses") List<SessionStatus> excludedStatuses
    );

    // Lấy tất cả lịch sử (DONE) để hiển thị trong trang lịch sử
    List<MentoringSession> findByStudentAndStatusOrderByStartTimeDesc(User student, SessionStatus status);

    long countByStudentAndStatus(User student, SessionStatus status);

    // =========================================================
    // 3. LOGIC GIẢNG VIÊN
    // =========================================================
    List<MentoringSession> findByLecturerAndStatusInOrderByStartTimeAsc(User lecturer, List<SessionStatus> statuses);

    long countByLecturerAndStatus(User lecturer, SessionStatus status);
}