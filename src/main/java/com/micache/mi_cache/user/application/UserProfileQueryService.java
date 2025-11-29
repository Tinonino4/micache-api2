package com.micache.mi_cache.user.application;

import org.springframework.stereotype.Service;

import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.user.domain.UserProfile;
import com.micache.mi_cache.user.domain.repository.UserAccountRepository;
import com.micache.mi_cache.user.domain.repository.UserProfileRepository;
import com.micache.mi_cache.user.interfaces.rest.dto.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileQueryService {

    private final UserAccountRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return UserProfileResponse.fromDomain(profile);
    }
}
