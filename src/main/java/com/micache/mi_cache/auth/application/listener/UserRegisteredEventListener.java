package com.micache.mi_cache.auth.application.listener;

import com.micache.mi_cache.auth.domain.events.UserRegisteredEvent;
import com.micache.mi_cache.shared.infrastructure.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private static final String TEMPLATE_PATH = "src/main/resources/templates/email_template_confirmacion.html";

    private final EmailService emailService;

    @EventListener(UserRegisteredEvent.class)
    public void onUserRegistered(UserRegisteredEvent event) {
        String emailTemplate;
        try {
            emailTemplate = Files.readString(Paths.get(TEMPLATE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template", e);
        }

        String emailContent = emailTemplate.replace("{{CONFIRMATION_LINK}}", event.confirmationLink());

        emailService.sendEmail(event.email(), "Email confirmation", emailContent);
    }
}
