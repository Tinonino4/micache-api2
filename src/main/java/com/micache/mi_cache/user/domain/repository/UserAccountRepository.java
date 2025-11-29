package com.micache.mi_cache.user.domain.repository;

import java.util.Optional;

import com.micache.mi_cache.user.domain.User;

public interface UserAccountRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}
