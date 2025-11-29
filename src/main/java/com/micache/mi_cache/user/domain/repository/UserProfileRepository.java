package com.micache.mi_cache.user.domain.repository;

import java.util.Optional;

import com.micache.mi_cache.user.domain.UserProfile;

public interface UserProfileRepository {
    Optional<UserProfile> findByUserId(Long userId);
    UserProfile save(UserProfile profile);
}
