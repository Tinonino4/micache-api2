package com.micache.mi_cache.service;

import com.micache.mi_cache.dto.UserProfileResponse;
import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import com.micache.mi_cache.security.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return new UserProfileResponse(
                profile.getName(),
                profile.getContactEmail(),
                profile.getCity(),
                profile.getEducation(),
                profile.getJobTitle()
        );
    }
}
