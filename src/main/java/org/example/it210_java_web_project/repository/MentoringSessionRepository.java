package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.MentoringSession;
import org.example.it210_java_web_project.model.SessionStatus;
import org.example.it210_java_web_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    // =========================================================
    // 1. LOGIC ĐẶT LỊCH: Kiểm tra Giảng viên có bị trùng giờ không
    // =========================================================
    @Query("""
        SELECT COUNT(ms) > 0
        FROM MentoringSession ms
        WHERE ms.lecturer = :lecturer
        AND ms.status <> 'CANCELLED'
        AND (
            :startTime < ms.endTime
            AND
            :endTime > ms.startTime
        )
    """)
    boolean existsConflictSchedule(
            User lecturer,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // =========================================================
    // 2. LOGIC DASHBOARD: Lấy dữ liệu hiển thị cho Sinh viên
    // =========================================================

    // Lấy danh sách lịch hẹn (loại trừ các lịch đã bị Hủy), sắp xếp từ sớm nhất đến muộn nhất
    List<MentoringSession> findByStudentAndStatusNotOrderByStartTimeAsc(User student, SessionStatus status);

    // Đếm tổng số lịch hẹn theo trạng thái (dùng để hiển thị con số trên thẻ Thống kê)
    long countByStudentAndStatus(User student, SessionStatus status);
    // Lấy các yêu cầu PENDING và APPROVED của riêng giảng viên này, xếp theo thời gian
    List<MentoringSession> findByLecturerAndStatusInOrderByStartTimeAsc(User lecturer, List<SessionStatus> statuses);

    // Đếm số lượng yêu cầu đang chờ duyệt
    long countByLecturerAndStatus(User lecturer, SessionStatus status);
}