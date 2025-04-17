package com.micache.mi_cache.service;

import com.micache.mi_cache.exception.InvalidTokenException;
import com.micache.mi_cache.exception.TokenExpiredException;
import com.micache.mi_cache.model.ConfirmationToken;
import com.micache.mi_cache.model.User;
import com.micache.mi_cache.repository.ConfirmationTokenRepository;
import com.micache.mi_cache.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public void validateToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid confirmation token."));

        if (confirmationToken.isConfirmed()) {
            throw new InvalidTokenException("Token has already been used.");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token has expired.");
        }

        confirmToken(confirmationToken);
        enableUser(confirmationToken.getUser());
    }

    private void confirmToken(ConfirmationToken token) {
        token.setConfirmed(true);
        tokenRepository.save(token);
    }

    private void enableUser(User user) {
        user.setActive(true);
        userRepository.save(user);
    }
}
