package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.MentoringSession;
import org.example.it210_java_web_project.model.SessionStatus;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.repository.UserRepository;
import org.example.it210_java_web_project.service.mentoringsesion.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final MentoringSessionService sessionService;
    private final UserRepository userRepository;

    // HIỂN THỊ DASHBOARD
    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Lấy dữ liệu
        List<MentoringSession> sessions = sessionService.getTeacherDashboardSessions(teacher);
        long pendingCount = sessionService.countTeacherPendingSessions(teacher);
        long completedCount = sessionService.countTeacherCompletedSessions(teacher);
        long approvedCount = sessions.stream().filter(s -> s.getStatus() == SessionStatus.APPROVED).count();

        // Tạo cục thống kê (Stats) cho 3 thẻ Card phía trên
        List<Map<String, Object>> stats = List.of(
                Map.of("label", "Yêu cầu chờ duyệt", "value", pendingCount, "icon", "bi-hourglass-split", "color", "warning"),
                Map.of("label", "Ca tư vấn sắp tới", "value", approvedCount, "icon", "bi-calendar-check", "color", "info"),
                Map.of("label", "Đã hoàn thành", "value", completedCount, "icon", "bi-check2-circle", "color", "success")
        );

        // Đẩy ra View
        String teacherName = teacher.getProfile() != null ? teacher.getProfile().getFullName() : teacher.getUsername();
        model.addAttribute("teacherName", teacherName);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("sessions", sessions);
        model.addAttribute("stats", stats);

        return "teacher/dashboard"; // Trỏ đúng tên file HTML của bạn
    }

    // XỬ LÝ NÚT CHẤP NHẬN YÊU CẦU
    @GetMapping("/mentoring/approve/{id}")
    public String approveSession(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User teacher = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            sessionService.updateSessionStatus(id, SessionStatus.APPROVED, teacher);
            redirectAttributes.addFlashAttribute("success", "Đã phê duyệt ca tư vấn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teacher/dashboard";
    }

    // XỬ LÝ NÚT TỪ CHỐI YÊU CẦU
    @GetMapping("/mentoring/reject/{id}")
    public String rejectSession(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User teacher = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            sessionService.updateSessionStatus(id, SessionStatus.CANCELLED, teacher); // Hoặc REJECTED tùy Enum của bạn
            redirectAttributes.addFlashAttribute("success", "Đã từ chối ca tư vấn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teacher/dashboard";
    }
}