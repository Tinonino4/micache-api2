package com.micache.mi_cache.security.application;

import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.auth.domain.events.UserRegisteredEvent;
import com.micache.mi_cache.security.model.ConfirmationToken;
import com.micache.mi_cache.security.repository.ConfirmationTokenRepository;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.shared.domain.EventBus;
import com.micache.mi_cache.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ConfirmationTokenCreationListener {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private final EventBus eventBus;

    public ConfirmationTokenCreationListener(ConfirmationTokenRepository confirmationTokenRepository,
                                             UserRepository userRepository,
                                             EventBus eventBus) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Value("${app.confirmation-url}")
    private String confirmationUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(UserRegistrationCompletedEvent event) {
        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Si el usuario ya está activo (ej. vino de Google), no necesitamos confirmación.
        if (user.isActive()) { // Asumiendo que User tiene el método isActive() o isEnabled()
            return;
        }

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
