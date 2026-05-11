package org.example.it210_java_web_project.service.mentoringsesion;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.AppointmentDTO;
import org.example.it210_java_web_project.model.*;
import org.example.it210_java_web_project.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringSessionService {

    // Inject tất cả các Repository cần thiết cho CORE-05 và CORE-06
    private final MentoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AcademicEvaluationRepository evaluationRepository;
    private final EquipmentRepository equipmentRepository;
    private final BorrowingRecordRepository borrowingRepository;

    // ========================================================
    // CORE-05: ĐẶT LỊCH CỐ VẤN & CHỐNG XUNG ĐỘT TÀI NGUYÊN
    // ========================================================
    public void createSession(AppointmentDTO request, User student) {

        // 1. Tìm giảng viên từ ID
        User lecturer = userRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên"));

        // Lấy thời gian từ form và tự động set thời gian kết thúc (+2 tiếng)
        LocalDateTime startTime = request.getAppointmentTime();
        LocalDateTime endTime = startTime.plusHours(2);

        // 2. Chống đặt lịch trong quá khứ (Lớp bảo vệ Backend)
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể đặt lịch trong quá khứ!");
        }

        // 3. Chống overlap (trùng lặp) lịch của Giảng viên
        boolean conflict = sessionRepository.existsConflictSchedule(lecturer, startTime, endTime);
        if (conflict) {
            throw new RuntimeException("Giảng viên đã có lịch bận trong khung giờ này!");
        }

        // 4. Lưu vào Database với trạng thái mặc định là PENDING
        MentoringSession session = new MentoringSession();
        session.setStudent(student);
        session.setLecturer(lecturer);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setNote(request.getNote());
        session.setStatus(SessionStatus.PENDING); // Đảm bảo gán trạng thái chờ

        sessionRepository.save(session);
    }

    // ========================================================
    // LOGIC: LẤY DANH SÁCH LỊCH CHO SINH VIÊN
    // ========================================================
    public List<MentoringSession> getUpcomingSessions(User student) {
        // Lấy các lịch PENDING hoặc APPROVED, bỏ qua CANCELLED
        return sessionRepository.findByStudentAndStatusNotOrderByStartTimeAsc(student, SessionStatus.CANCELLED);
    }

    public long countPendingSessions(User student) {
        return sessionRepository.countByStudentAndStatus(student, SessionStatus.PENDING);
    }

    // ========================================================
    // LOGIC: HỦY LỊCH HẸN
    // ========================================================
    public void cancelSession(Long sessionId, User student) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn này."));

        // Bảo mật: Kiểm tra xem lịch này có đúng là của sinh viên đang đăng nhập không
        if (!session.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy lịch hẹn của người khác!");
        }

        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    // ========================================================
    // CORE-06: ĐÁNH GIÁ & TÍNH TOÀN VẸN DỮ LIỆU (TRANSACTION)
    // ========================================================
    // ========================================================
    // CORE-06: ĐÁNH GIÁ & TÍNH TOÀN VẸN DỮ LIỆU (TRANSACTION)
    // ========================================================
    @Transactional(rollbackFor = Exception.class)
    public void completeAndEvaluate(Long sessionId, String comment, Integer rating, List<Long> equipmentIds, User lecturer) {

        // 1. Lấy ca tư vấn ra
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Ca tư vấn không tồn tại trong hệ thống"));

        // 2. Đổi trạng thái thành DONE
        session.setStatus(SessionStatus.DONE);
        sessionRepository.save(session);

        // 3. Lưu bảng Đánh giá năng lực (Sử dụng @MapsId)
        AcademicEvaluation eval = new AcademicEvaluation();
        eval.setSession(session);
        eval.setLecturer(lecturer);
        eval.setComment(comment);
        eval.setRating(rating);
        eval.setCreatedAt(LocalDateTime.now());

        evaluationRepository.save(eval);

        // 4. Khởi tạo và Lưu Phiếu mượn thiết bị (Nếu Giảng viên có chọn thiết bị)
        if (equipmentIds != null && !equipmentIds.isEmpty()) {

            // Khởi tạo Phiếu mượn (Master)
            BorrowingRecord record = new BorrowingRecord();
            record.setSession(session);
            record.setStudent(session.getStudent());
            record.setStatus(BorrowStatus.APPROVED);
            record.setCreatedAt(LocalDateTime.now());

            // Khởi tạo danh sách Chi tiết phiếu mượn (Detail)
            List<BorrowingDetail> details = new java.util.ArrayList<>();

            for (Long eqId : equipmentIds) {
                Equipment equipment = equipmentRepository.findById(eqId)
                        .orElseThrow(() -> new RuntimeException("Lỗi: Thiết bị không tồn tại hoặc đã bị xóa"));

                BorrowingDetail detail = new BorrowingDetail();
                detail.setRecord(record); // Map ngược lại về Record
                detail.setEquipment(equipment);

                details.add(detail);
            }

            // Gắn danh sách Detail vào Record và lưu xuống Database
            record.setDetails(details);
            borrowingRepository.save(record); // Biến 'record' đã được khai báo chuẩn chỉnh!
        }
    }
    // ========================================================
    // LOGIC DASHBOARD GIẢNG VIÊN
    // ========================================================
    public List<MentoringSession> getTeacherDashboardSessions(User lecturer) {
        // Chỉ hiển thị các ca đang Chờ duyệt (PENDING) hoặc Đã duyệt (APPROVED) để chờ đánh giá
        return sessionRepository.findByLecturerAndStatusInOrderByStartTimeAsc(
                lecturer,
                List.of(SessionStatus.PENDING, SessionStatus.APPROVED)
        );
    }

    public long countTeacherPendingSessions(User lecturer) {
        return sessionRepository.countByLecturerAndStatus(lecturer, SessionStatus.PENDING);
    }

    public long countTeacherCompletedSessions(User lecturer) {
        return sessionRepository.countByLecturerAndStatus(lecturer, SessionStatus.DONE);
    }

    // ========================================================
    // LOGIC DUYỆT / TỪ CHỐI CA TƯ VẤN
    // ========================================================
    public void updateSessionStatus(Long sessionId, SessionStatus newStatus, User lecturer) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca tư vấn"));

        // Bảo mật: Đảm bảo chỉ giảng viên được phân công mới có quyền duyệt
        if (!session.getLecturer().getId().equals(lecturer.getId())) {
            throw new RuntimeException("Bạn không có quyền thao tác trên ca tư vấn này!");
        }

        session.setStatus(newStatus);
        sessionRepository.save(session);
    }
}