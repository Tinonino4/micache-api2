package com.micache.mi_cache.service;

import com.micache.mi_cache.dto.UserProfileResponse;
import com.micache.mi_cache.exception.ResourceNotFoundException;
import com.micache.mi_cache.model.User;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import com.micache.mi_cache.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return new UserProfileResponse(
                profile.getName(),
                profile.getSurname(),
                profile.getCity(),
                profile.getEducation(),
                profile.getJobTitle()
        );
    }
}
