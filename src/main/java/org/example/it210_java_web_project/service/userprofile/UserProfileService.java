package org.example.it210_java_web_project.service.userprofile;

import org.example.it210_java_web_project.model.UserProfile;

public interface UserProfileService {
    UserProfile getByUserId(Long userId);
    void save(UserProfile profile);
}
