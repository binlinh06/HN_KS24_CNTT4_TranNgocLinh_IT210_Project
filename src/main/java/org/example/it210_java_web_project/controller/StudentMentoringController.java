package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.AcademicProfileDTO;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.repository.UserRepository;
import org.example.it210_java_web_project.service.mentoringsesion.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentMentoringController {

    private final UserRepository userRepository;
    private final MentoringSessionService sessionService; // Đây là biến Service đã được tiêm vào

    // Hiển thị lịch sử ca tư vấn của sinh viên (CORE-07)
    @GetMapping("/student/history")
    public String showAcademicHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

        List<AcademicProfileDTO> historyProfiles = sessionService.getStudentAcademicHistory(student);
        model.addAttribute("profiles", historyProfiles);

        return "student/mentoring/history";
    }

    // 🔥 ĐÃ FIX LỖI STATIC: Gọi qua sessionService (viết thường)
    // Sửa lại Mapping cho khớp với nút Hủy trên Dashboard
    @GetMapping("/student/mentoring-sessions/cancel/{id}")
    public String cancelSession(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            // ✅ ĐÚNG: Gọi thông qua đối tượng 'sessionService'
            sessionService.cancelSession(id, userDetails.getUsername());

            redirectAttributes.addFlashAttribute("success", "Hủy lịch hẹn thành công. Khung giờ đã được giải phóng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}