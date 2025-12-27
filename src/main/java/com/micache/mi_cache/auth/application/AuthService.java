package com.micache.mi_cache.auth.application;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import com.micache.mi_cache.auth.domain.LoginResponse;
import com.micache.mi_cache.auth.domain.RefreshToken;
import com.micache.mi_cache.auth.domain.RegisterRequest;
import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.auth.infrastructure.RefreshTokenRepository;
import com.micache.mi_cache.security.exception.EmailAlreadyExistsException;
import com.micache.mi_cache.security.exception.InvalidPasswordException;
import com.micache.mi_cache.security.exception.InvalidTokenException;
import com.micache.mi_cache.security.exception.TokenExpiredException;
import com.micache.mi_cache.security.infrastructure.GoogleTokenVerifier;
import com.micache.mi_cache.shared.domain.EventBus;
import com.micache.mi_cache.security.service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.security.model.ConfirmationToken;
import com.micache.mi_cache.security.repository.ConfirmationTokenRepository;
import com.micache.mi_cache.security.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EventBus eventBus;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleTokenVerifier googleVerifier;

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    public AuthService(UserRepository userRepository,
                       ConfirmationTokenRepository confirmationTokenRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       EventBus eventBus,
                       GoogleTokenVerifier googleVerifier) {
        this.googleVerifier = googleVerifier;
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.eventBus = eventBus;
    }

    @Transactional
    public LoginResponse loginWithGoogle(String idToken) {
        // 1. Verificar el token con Google (External Service)
        GoogleTokenVerifier.GoogleUser googleUser = googleVerifier.verify(idToken);

        // 2. Buscar si el usuario ya existe
        return userRepository.findByEmail(googleUser.email())
                .map(existingUser -> {
                    // CASO A: Usuario existe -> Login normal
                    String accessToken = jwtService.generateToken(existingUser);
                    RefreshToken refreshToken = createRefreshToken(existingUser.getId());
                    return new LoginResponse(accessToken, refreshToken.getToken());
                })
                .orElseGet(() -> {
                    // CASO B: Usuario nuevo -> Registro automático
                    User newUser = registerGoogleUser(googleUser);

                    String accessToken = jwtService.generateToken(newUser);
                    RefreshToken refreshToken = createRefreshToken(newUser.getId());

                    return new LoginResponse(accessToken, refreshToken.getToken());
                });
    }

    private User registerGoogleUser(GoogleTokenVerifier.GoogleUser googleUser) {
        User user = new User();
        user.setName(googleUser.name());
        user.setEmail(googleUser.email());
        // Contraseña dummy fuerte (nunca se usará, pero satisface la DB)
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole("USER");
        user.setActive(true); // Los emails de Google ya vienen verificados, así que lo activamos directo

        User savedUser = userRepository.save(user);

        // IMPORTANTE: Publicar evento para que se cree el UserProfile
        // Nota: Pasamos googleUser.pictureUrl() si quieres guardarla en el perfil luego
        eventBus.publish(new UserRegistrationCompletedEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                googleUser.name()
        ));

        return savedUser;
    }

    public LoginResponse login(String email, String password) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User user = userRepository.findByEmail(email).orElseThrow();

        // 1. Generar Access Token (JWT)
        String accessToken = jwtService.generateToken(user);

        // 2. Generar Refresh Token (Opaco)
        RefreshToken refreshToken = createRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    public LoginResponse refreshToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration) // Verifica si caducó
                .map(refreshToken -> {
                    // 1. Buscamos al usuario dueño del token
                    User user = userRepository.findById(refreshToken.getUserId())
                            .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado"));

                    // 2. Generamos nuevo Access Token
                    String newAccessToken = jwtService.generateToken(user);

                    // 3. (Opcional pero recomendado) Rotación de Refresh Token
                    // Borramos el usado y damos uno nuevo para mayor seguridad
                    // refreshTokenRepository.delete(refreshToken);
                    // RefreshToken newRefreshToken = createRefreshToken(user.getId());

                    // Por simplicidad inicial, devolvemos el mismo Refresh Token si sigue vivo
                    return new LoginResponse(newAccessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new InvalidTokenException("Refresh token no encontrado en base de datos"));
    }

    private RefreshToken createRefreshToken(Long userId) {
        // Limpiamos tokens viejos (opcional, para no llenar la BD)
        // refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.create(userId, refreshTokenDurationMs);
        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token ha expirado. Por favor inicie sesión nuevamente.");
        }
        return token;
    }


    @Transactional
    public void register(RegisterRequest request) {
        // Validar si el email ya existe
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already registered.");
        }

        // Validar formato de password
        if (!isPasswordValid(request.password())) {
            throw new InvalidPasswordException("Password must contain at least one uppercase letter, one lowercase letter and one number.");
        }

        // Crear usuario
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(false);
        user.setRole("USER");

        userRepository.save(user);
        eventBus.publish(new UserRegistrationCompletedEvent(user.getId(), user.getEmail(), request.name()));
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
