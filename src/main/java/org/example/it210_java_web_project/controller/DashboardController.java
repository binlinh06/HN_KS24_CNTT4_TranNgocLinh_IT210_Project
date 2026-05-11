package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.MentoringSession;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.repository.UserRepository;
import org.example.it210_java_web_project.service.mentoringsesion.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor // Thêm annotation này để Spring tự động Inject các dependency
public class DashboardController {

    private final MentoringSessionService mentoringSessionService;
    private final UserRepository userRepository;

    // ==========================================
    // 1. DASHBOARD ADMIN
    // ==========================================
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    // ==========================================
    // 2. DASHBOARD GIẢNG VIÊN (TEACHER)
//    // ==========================================
//    @GetMapping("/teacher/dashboard")
//    public String teacherDashboard() {
//        return "teacher/dashboard";
//    }

    // ==========================================
    // 3. DASHBOARD SINH VIÊN (STUDENT)
    // ==========================================
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Lấy thông tin sinh viên đang đăng nhập từ Security Context
        User student = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        // 2. Truy vấn danh sách lịch cố vấn sắp tới từ Service
        List<MentoringSession> upcoming = mentoringSessionService.getUpcomingSessions(student);

        // 3. Đẩy dữ liệu lên Model để Thymeleaf render ra giao diện
        model.addAttribute("student", student);
        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("advisorCount", upcoming.size()); // Cập nhật số đếm lịch cố vấn

        // (Tùy chọn) Dữ liệu cứng tạm thời cho các thẻ thống kê khác
        model.addAttribute("borrowingCount", 2);
        model.addAttribute("historyCount", 15);

        return "student/dashboard";
    }
}