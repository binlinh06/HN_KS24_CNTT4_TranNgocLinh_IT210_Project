package org.example.it210_java_web_project.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {

            String role = authority.getAuthority();

            // ROLE_ADMIN
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return;
            }

            // ROLE_TEACHER
            if (role.equals("ROLE_LECTURER")) {
                response.sendRedirect("/teacher/dashboard");
                return;
            }

            // ROLE_STUDENT
            if (role.equals("ROLE_STUDENT")) {
                response.sendRedirect("/student/dashboard");
                return;
            }
        }

        // fallback
        response.sendRedirect("/auth/login?error=true");
    }
}
