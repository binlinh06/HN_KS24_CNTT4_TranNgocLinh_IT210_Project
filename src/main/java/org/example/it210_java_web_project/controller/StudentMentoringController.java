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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentMentoringController {

    private final UserRepository userRepository;
    private final MentoringSessionService sessionService;

    // Hiển thị lịch sử ca tư vấn của sinh viên (CORE-07)
    @GetMapping("/student/history")
    public String showAcademicHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

        List<AcademicProfileDTO> historyProfiles = sessionService.getStudentAcademicHistory(student);
        model.addAttribute("profiles", historyProfiles);

        return "student/mentoring/history";
    }
}