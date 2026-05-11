package org.example.it210_java_web_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.model.UserProfile;
import org.example.it210_java_web_project.repository.UserRepository;
import org.example.it210_java_web_project.service.userprofile.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping
    public String viewProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy User từ username đang đăng nhập
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy Profile theo userId
        UserProfile profile = profileService.getByUserId(user.getId());

        // Nếu chưa có profile (phòng trường hợp lỗi cascade), tạo mới object để form không bị null
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(user.getId());
        }

        model.addAttribute("profile", profile);
        model.addAttribute("student", user); // Đẩy thêm user để layout hiển thị tên/avatar ở navbar

        // Trả về file HTML (Bạn kiểm tra đúng đường dẫn templates/student/profile.html nhé)
        return "student/profile/form";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("profile") UserProfile profile,
                         BindingResult bindingResult, // Bắt lỗi tại đây
                         @AuthenticationPrincipal UserDetails userDetails,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Nếu có lỗi, nạp lại dữ liệu User để layout không lỗi và trả về trang form
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            model.addAttribute("student", user);
            return "student/profile/form"; // Trả về view, KHÔNG redirect
        }

        try {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            profile.setUserId(user.getId());
            profileService.save(profile);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "student/profile/form";
        }

        return "redirect:/student/profile";
    }
}