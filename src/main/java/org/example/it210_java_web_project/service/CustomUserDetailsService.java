package org.example.it210_java_web_project.service;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(

                user.getUsername(),

                user.getPasswordHash(),

                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().getName()
                        )
                )
        );
    }
}