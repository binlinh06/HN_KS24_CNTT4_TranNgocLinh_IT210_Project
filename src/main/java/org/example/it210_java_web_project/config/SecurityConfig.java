package org.example.it210_java_web_project.config;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomLoginSuccessHandler successHandler;
    // đọc user từ database
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers(
                                "/auth/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // ADMIN
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // TEACHER
                        .requestMatchers("/teacher/**")
                        .hasRole("LECTURER")

                        // STUDENT
                        .requestMatchers("/student/**")
                        .hasRole("STUDENT")

                        // request khác cần login
                        .anyRequest()
                        .authenticated()
                )

                // LOGIN
                .formLogin(form -> form

                        .loginPage("/auth/login")

                        .loginProcessingUrl("/auth/login")

                        .usernameParameter("username")

                        .passwordParameter("password")

                        .successHandler(successHandler)

                        .failureUrl("/auth/login?error=true")

                        .permitAll()
                )

                // LOGOUT
                .logout(logout -> logout

                        .logoutUrl("/auth/logout")

                        .logoutSuccessUrl(
                                "/auth/login?logout=true"
                        )

                        .permitAll()
                )

                // ACCESS DENIED
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/auth/403")
                );

        return http.build();
    }

    // AUTH PROVIDER
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider auth =
                new DaoAuthenticationProvider(userDetailsService);

        auth.setPasswordEncoder(passwordEncoder());

        return auth;
    }

    // PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}