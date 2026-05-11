package org.example.it210_java_web_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.LoginRequest;
import org.example.it210_java_web_project.dto.RegisterRequest;
import org.example.it210_java_web_project.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthWebController {

    private final AuthService authService;

    // ================= REGISTER =================

    @GetMapping("/register")
    public String showRegister(Model model) {

        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid RegisterRequest req,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {

        // check confirm password
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            result.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "Mật khẩu không khớp"
            );
        }

        // validate error
        if (result.hasErrors()) {
            model.addAttribute("registerRequest", req);
            return "auth/register";
        }

        String response = authService.register(req);

        // register success
        if (response.equals("SUCCESS")) {

            redirect.addFlashAttribute(
                    "success",
                    "Đăng ký thành công! Hãy đăng nhập."
            );

            return "redirect:/auth/login";
        }

        // register fail
        model.addAttribute("error", response);
        model.addAttribute("registerRequest", req);

        return "auth/register";
    }

    // ================= LOGIN =================

    @GetMapping("/login")
    public String showLogin(
            Model model
    ) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    // ================= ACCESS DENIED =================

    @GetMapping("/403")
    public String accessDenied() {
        return "403/403";
    }
}