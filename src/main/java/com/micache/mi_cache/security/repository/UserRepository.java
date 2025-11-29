package com.micache.mi_cache.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.user.domain.repository.UserAccountRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserAccountRepository {
    @Override
    Optional<User> findByEmail(String email);
}
