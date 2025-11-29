package com.micache.mi_cache.security.application;

import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.auth.domain.events.UserRegisteredEvent;
import com.micache.mi_cache.security.model.ConfirmationToken;
import com.micache.mi_cache.security.repository.ConfirmationTokenRepository;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.shared.domain.EventBus;
import com.micache.mi_cache.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConfirmationTokenCreationListener {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private final EventBus eventBus;

    @Value("${app.confirmation-url}")
    private String confirmationUrl;

    @EventListener
    @Transactional
    public void handle(UserRegistrationCompletedEvent event) {
        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .confirmed(false)
                .build();

        confirmationTokenRepository.save(confirmationToken);

        String link = confirmationUrl + "?token=" + token;
        eventBus.publish(new UserRegisteredEvent(event.email(), event.name(), link));
    }
}
