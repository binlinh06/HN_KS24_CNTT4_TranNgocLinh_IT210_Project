package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.AppointmentDTO;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.repository.DepartmentRepository; // 🔥 ĐÃ THÊM
import org.example.it210_java_web_project.repository.UserRepository;
import org.example.it210_java_web_project.service.mentoringsesion.MentoringSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/mentoring-sessions")
public class AppointmentController {

    private final MentoringSessionService mentoringSessionService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository; // 🔥 ĐÃ THÊM: Tiêm Repository của Khoa

    @GetMapping("/create")
    public String showForm(Model model) {
        // Lấy danh sách Khoa và Giảng viên để đổ ra màn hình
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("lecturers", userRepository.findByRoleName("LECTURER"));
        model.addAttribute("appointmentRequest", new AppointmentDTO());

        return "student/mentoring-sessions/create";
    }

    @PostMapping("/create")
    public String createAppointment(
            @ModelAttribute("appointmentRequest") AppointmentDTO request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            // Lấy thông tin sinh viên đang đăng nhập
            User student = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

            // Gọi logic tạo lịch từ Service (Kiểm tra điều kiện CORE-05 nằm ở đây)
            mentoringSessionService.createSession(request, student);

            // NẾU THÀNH CÔNG: Gắn thông báo và quay về Dashboard
            redirectAttributes.addFlashAttribute("success", "Đặt lịch cố vấn thành công!");
            return "redirect:/student/dashboard";

        } catch (Exception e) {
            // NẾU THẤT BẠI: (Ví dụ chọn giờ trong quá khứ hoặc bị trùng giờ)
            model.addAttribute("error", e.getMessage());

            // 🔥 QUAN TRỌNG: Phải nạp lại cả 2 danh sách này để giao diện không bị sập khi load lại
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("lecturers", userRepository.findByRoleName("LECTURER"));

            // Trả về lại trang HTML Đặt lịch
            return "student/mentoring-sessions/create";
        }
    }

    @GetMapping("/cancel/{id}")
    public String cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Chỉ sinh viên tạo ra lịch này mới được quyền hủy
            User student = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            mentoringSessionService.cancelSession(id, student);

            redirectAttributes.addFlashAttribute("success", "Đã hủy lịch hẹn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}