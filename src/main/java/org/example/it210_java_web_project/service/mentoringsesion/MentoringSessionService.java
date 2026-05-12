package org.example.it210_java_web_project.service.mentoringsesion;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.AcademicProfileDTO;
import org.example.it210_java_web_project.dto.AppointmentDTO;
import org.example.it210_java_web_project.model.*;
import org.example.it210_java_web_project.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringSessionService {

    private final MentoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AcademicEvaluationRepository evaluationRepository;
    private final EquipmentRepository equipmentRepository;
    private final BorrowingRecordRepository borrowingRepository;

    // CORE-05: ĐẶT LỊCH
    public void createSession(AppointmentDTO request, User student) {
        User lecturer = userRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên"));

        LocalDateTime startTime = request.getAppointmentTime();
        LocalDateTime endTime = startTime.plusHours(2);

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể đặt lịch trong quá khứ!");
        }

        // Đã sửa: Truyền danh sách trạng thái muốn loại trừ (CANCELLED) vào câu Query
        // Dựa vào SessionStatus Enum hiện tại của bạn, chỉ có CANCELLED là trạng thái giải phóng slot
        List<SessionStatus> excludedStatuses = List.of(SessionStatus.CANCELLED);

        if (sessionRepository.existsConflictSchedule(lecturer, startTime, endTime, excludedStatuses)) {
            throw new RuntimeException("Giảng viên đã có lịch bận trong khung giờ này!");
        }

        MentoringSession session = new MentoringSession();
        session.setStudent(student);
        session.setLecturer(lecturer);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setNote(request.getNote());
        session.setStatus(SessionStatus.PENDING);

        sessionRepository.save(session);
    }

    // CORE-09: HỦY LỊCH & GIẢI PHÓNG SLOT
    @Transactional
    public void cancelSession(Long sessionId, String username) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        if (!session.getStudent().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên lịch của người khác!");
        }

        // Chặn hủy nếu còn dưới 24h
        if (session.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RuntimeException("Chỉ được phép hủy lịch trước 24 giờ tính từ thời điểm bắt đầu!");
        }

        if (session.getStatus() == SessionStatus.DONE) {
            throw new RuntimeException("Lịch đã hoàn thành, không thể hủy!");
        }

        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    // CORE-06: ĐÁNH GIÁ & CẤP THIẾT BỊ
    @Transactional(rollbackFor = Exception.class)
    public void completeAndEvaluate(Long sessionId, String comment, Integer rating, List<Long> equipmentIds, User lecturer) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Ca tư vấn không tồn tại"));

        session.setStatus(SessionStatus.DONE);
        sessionRepository.save(session);

        AcademicEvaluation eval = new AcademicEvaluation();
        eval.setSession(session);
        eval.setLecturer(lecturer);
        eval.setComment(comment);
        eval.setRating(rating);
        eval.setCreatedAt(LocalDateTime.now());
        evaluationRepository.save(eval);

        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            BorrowingRecord record = new BorrowingRecord();
            record.setSession(session);
            record.setStudent(session.getStudent());
            record.setStatus(BorrowStatus.APPROVED);
            record.setCreatedAt(LocalDateTime.now());

            List<BorrowingDetail> details = new ArrayList<>();
            for (Long eqId : equipmentIds) {
                details.add(createBorrowingDetail(eqId, session, record));
            }
            record.setDetails(details);
            borrowingRepository.save(record);
        }
    }

    // LOGIC DASHBOARD SINH VIÊN
    public List<MentoringSession> getUpcomingSessions(User student) {
        // Đã sửa: Tạo danh sách các trạng thái KHÔNG muốn lấy ra cho Dashboard "Sắp tới"
        List<SessionStatus> excludedStatuses = List.of(SessionStatus.DONE, SessionStatus.CANCELLED);
        return sessionRepository.findUpcomingSessions(student, excludedStatuses);
    }

    // LOGIC DASHBOARD GIẢNG VIÊN
    public List<MentoringSession> getTeacherDashboardSessions(User lecturer) {
        return sessionRepository.findByLecturerAndStatusInOrderByStartTimeAsc(
                lecturer, List.of(SessionStatus.PENDING, SessionStatus.APPROVED));
    }

    public void updateSessionStatus(Long sessionId, SessionStatus newStatus, User lecturer) {
        MentoringSession session = sessionRepository.findById(sessionId).orElseThrow();
        if (!session.getLecturer().getId().equals(lecturer.getId())) {
            throw new RuntimeException("Access Denied");
        }
        session.setStatus(newStatus);
        sessionRepository.save(session);
    }

    private BorrowingDetail createBorrowingDetail(Long equipmentId, MentoringSession session, BorrowingRecord record) {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        BorrowingDetail detail = new BorrowingDetail();
        BorrowingDetailId detailId = new BorrowingDetailId();
        detailId.setRecordId(session.getId());
        detailId.setEquipmentId(equipment.getId());
        detail.setId(detailId);
        detail.setRecord(record);
        detail.setEquipment(equipment);
        detail.setQuantity(1);
        return detail;
    }

    @Transactional(readOnly = true)
    public List<AcademicProfileDTO> getStudentAcademicHistory(User student) {
        List<MentoringSession> history = sessionRepository
                .findByStudentAndStatusInOrderByStartTimeDesc(
                        student,
                        List.of(SessionStatus.DONE, SessionStatus.CANCELLED, SessionStatus.REJECTED)
                );
        List<AcademicProfileDTO> dtos = new ArrayList<>();
        for (MentoringSession s : history) {
            AcademicProfileDTO dto = new AcademicProfileDTO();
            dto.setSession(s);
            evaluationRepository.findById(s.getId()).ifPresent(dto::setEvaluation);

            borrowingRepository.findById(s.getId()).ifPresent(record -> {
                // 🔥 THÊM DÒNG NÀY ĐỂ GẮN PHIẾU MƯỢN VÀO DTO
                dto.setBorrowingRecord(record);

                if (record.getDetails() != null) {
                    dto.setBorrowedEquipments(record.getDetails().stream().map(BorrowingDetail::getEquipment).toList());
                }
            });
            dtos.add(dto);
        }
        return dtos;
    }

    // ========================================================
    // BỔ SUNG LOGIC THỐNG KÊ CHO GIẢNG VIÊN
    // ========================================================

    public long countTeacherPendingSessions(User lecturer) {
        // Đếm các ca đang ở trạng thái PENDING của giảng viên này
        return sessionRepository.countByLecturerAndStatus(lecturer, SessionStatus.PENDING);
    }

    public long countTeacherCompletedSessions(User lecturer) {
        // Đếm các ca đã hoàn thành (DONE) của giảng viên này
        return sessionRepository.countByLecturerAndStatus(lecturer, SessionStatus.DONE);
    }
}