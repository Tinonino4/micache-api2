package com.micache.mi_cache.user.infrastructure.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.micache.mi_cache.user.domain.UserProfile;
import com.micache.mi_cache.user.domain.repository.UserProfileRepository;

@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfile, Long>, UserProfileRepository {
    @Override
    Optional<UserProfile> findByUserId(Long userId);
}
