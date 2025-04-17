package com.micache.mi_cache.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import com.micache.mi_cache.dto.RegisterRequest;
import com.micache.mi_cache.exception.EmailAlreadyExistsException;
import com.micache.mi_cache.exception.InvalidPasswordException;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.micache.mi_cache.model.ConfirmationToken;
import com.micache.mi_cache.model.User;
import com.micache.mi_cache.repository.ConfirmationTokenRepository;
import com.micache.mi_cache.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    private final UserProfileRepository userProfileRepository;

    @Value("${app.confirmation-url}")
    private String confirmationUrl;

    public String login(String email, String password) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email).orElseThrow();
        return jwtService.generateToken(user);
    }

    public void register(RegisterRequest request) {
        // Validar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered.");
        }

        // Validar formato de password
        if (!isPasswordValid(request.getPassword())) {
            throw new InvalidPasswordException("Password must contain at least one uppercase letter, one lowercase letter and one number.");
        }

        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);
        user.setRole("USER");

        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setName(request.getName());
        profile.setSurname(request.getSurname());
        profile.setPhoto("https://randomuser.me/api/portraits/lego/1.jpg");

        userProfileRepository.save(profile);

        // Crear Confirmation Token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .confirmed(false)
                .build();

        confirmationTokenRepository.save(confirmationToken);

        // Leer template y enviar email
        String link = confirmationUrl + "?token=" + token;
        String emailTemplate;
        try {
            emailTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/email_template_confirmacion.html")));
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template", e);
        }

        String emailContent = emailTemplate.replace("{{CONFIRMATION_LINK}}", link);

        emailService.sendEmail(user.getEmail(), "Email confirmation", emailContent);
    }

    public boolean confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        if (confirmationToken.isConfirmed() || confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        confirmationToken.setConfirmed(true);
        confirmationToken.getUser().setActive(true);
        confirmationTokenRepository.save(confirmationToken);
        userRepository.save(confirmationToken.getUser());
        return true;
    }

    private boolean isPasswordValid(String password) {
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasUppercase && hasLowercase && hasDigit;
    }
}
