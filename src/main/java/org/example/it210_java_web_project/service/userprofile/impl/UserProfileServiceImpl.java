package org.example.it210_java_web_project.service.userprofile.impl;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.UserProfile;
import org.example.it210_java_web_project.repository.UserProfileRepository;
import org.example.it210_java_web_project.service.userprofile.UserProfileService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;

    @Override
    public UserProfile getByUserId(Long userId) {
        return repository.findById(userId).orElse(new UserProfile());
    }

    @Override
    public void save(UserProfile profile) {
        repository.save(profile);
    }
}
