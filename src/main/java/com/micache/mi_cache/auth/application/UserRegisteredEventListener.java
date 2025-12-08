package com.micache.mi_cache.auth.application;

import com.micache.mi_cache.auth.domain.events.UserRegisteredEvent;
import com.micache.mi_cache.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class UserRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

    private final EmailService emailService;
    private final ResourceLoader resourceLoader;

    public UserRegisteredEventListener(EmailService emailService, ResourceLoader resourceLoader) {
        this.emailService = emailService;
        this.resourceLoader = resourceLoader;
    }

    @Async
    @EventListener
    public void handle(UserRegisteredEvent event) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/email_template_confirmacion.html");
            String emailTemplate = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            String emailContent = emailTemplate.replace("{{CONFIRMATION_LINK}}", event.confirmationLink());

            emailService.sendEmail(event.email(), "Bienvenido a MiCache - Confirma tu cuenta", emailContent);
            log.info("Email de confirmación enviado a {}", event.email());

        } catch (IOException e) {
            log.error("Error leyendo la plantilla de email o enviando el correo", e);
            // TODO:reencolar el evento para reintentarlo
        }
    }
}
