package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
