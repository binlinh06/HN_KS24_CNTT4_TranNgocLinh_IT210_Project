package org.example.it210_java_web_project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    // password đã hash bằng BCrypt
    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // ROLE
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // PROFILE
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}