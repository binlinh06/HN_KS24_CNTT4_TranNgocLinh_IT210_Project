package org.example.it210_java_web_project.controller;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService profileService;
    private final UserRepository userRepository;

    // 🔥 HÀM MỞ GIAO DIỆN (Lúc nãy bị xóa mất)
    @GetMapping
    public String viewProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = profileService.getByUserId(user.getId());

        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(user.getId());
        }

        model.addAttribute("profile", profile);
        model.addAttribute("student", user);

        return "student/profile/form";
    }

    // 🔥 HÀM CẬP NHẬT HỒ SƠ (Đã tích hợp Nhóm kiểm tra UpdateAction)
    @PostMapping("/update")
    public String update(
            @Validated(UserProfile.UpdateAction.class) @ModelAttribute("profile") UserProfile profile,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            model.addAttribute("student", user);
            return "student/profile/form";
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