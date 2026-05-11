package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.MentoringSession;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.repository.EquipmentRepository;
import org.example.it210_java_web_project.repository.MentoringSessionRepository;
import org.example.it210_java_web_project.repository.UserRepository;
import org.example.it210_java_web_project.service.mentoringsesion.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teacher/mentoring")
@RequiredArgsConstructor
public class TeacherMentoringController {

    private final MentoringSessionService sessionService;
    private final MentoringSessionRepository sessionRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    // 1. Hiển thị màn hình Đánh giá
    @GetMapping("/evaluate/{id}")
    public String showEvaluateForm(@PathVariable Long id, Model model) {
        MentoringSession session = sessionRepository.findById(id).orElseThrow();

        // 🔥 ĐỔI TÊN BIẾN TẠI ĐÂY:
        model.addAttribute("mentoringSession", session);
        model.addAttribute("equipments", equipmentRepository.findAll());
        return "teacher/mentoring/evaluate";
    }

    // 2. Nút Submit Đánh giá (Kích hoạt Transaction)
    @PostMapping("/evaluate/{id}")
    public String submitEvaluation(
            @PathVariable Long id,
            @RequestParam String comment,
            @RequestParam Integer rating,
            @RequestParam(required = false) List<Long> equipmentIds,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User lecturer = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

            // Gọi hàm @Transactional ở Service
            sessionService.completeAndEvaluate(id, comment, rating, equipmentIds, lecturer);

            redirectAttributes.addFlashAttribute("success", "Lưu đánh giá và đóng ca tư vấn thành công!");
            return "redirect:/teacher/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đồng bộ hệ thống: " + e.getMessage());
            return "redirect:/teacher/mentoring/evaluate/" + id;
        }
    }
}