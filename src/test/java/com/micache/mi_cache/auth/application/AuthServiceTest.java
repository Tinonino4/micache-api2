package com.micache.mi_cache.auth.application;

import com.micache.mi_cache.auth.domain.LoginResponse;
import com.micache.mi_cache.auth.domain.RefreshToken;
import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.auth.infrastructure.RefreshTokenRepository;
import com.micache.mi_cache.security.exception.InvalidTokenException;
import com.micache.mi_cache.security.exception.TokenExpiredException;
import com.micache.mi_cache.security.infrastructure.GoogleTokenVerifier;
import com.micache.mi_cache.security.repository.ConfirmationTokenRepository;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.security.service.JwtService;
import com.micache.mi_cache.shared.domain.EventBus;
import com.micache.mi_cache.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ConfirmationTokenRepository confirmationTokenRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EventBus eventBus;
    @Mock private GoogleTokenVerifier googleVerifier;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                confirmationTokenRepository,
                refreshTokenRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                eventBus,
                googleVerifier
                // AppleVerifier si lo añadiste, ponlo aquí mockeado o null
        );
        // Inyectamos el valor de configuración @Value
        ReflectionTestUtils.setField(authService, "refreshTokenDurationMs", 60000L);
    }

    @Test
    @DisplayName("Login con Google - Usuario Nuevo: Debe registrar y publicar evento")
    void loginWithGoogle_NewUser() {
        // GIVEN
        String tokenGoogle = "token_valido";
        String email = "google@user.com";
        GoogleTokenVerifier.GoogleUser gUser = new GoogleTokenVerifier.GoogleUser(email, "Google User", "photo.jpg");

        when(googleVerifier.verify(tokenGoogle)).thenReturn(gUser);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty()); // No existe
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(99L);
            return u;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("access_token_jwt");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        LoginResponse response = authService.loginWithGoogle(tokenGoogle);

        // THEN
        assertNotNull(response);
        assertNotNull(response.accessToken());

        // Verificar que se publicó el evento de registro para inicializar métricas y perfil
        verify(eventBus).publish(any(UserRegistrationCompletedEvent.class));
    }

    @Test
    @DisplayName("Refresh Token - Éxito: Debe generar nuevo access token")
    void refreshToken_Success() {
        // GIVEN
        String tokenStr = "refresh_token_uuid";
        RefreshToken rt = new RefreshToken();
        rt.setToken(tokenStr);
        rt.setUserId(1L);
        rt.setExpiryDate(Instant.now().plusSeconds(60)); // No expirado

        User user = new User(); user.setId(1L); user.setEmail("test@test.com");

        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(rt));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("new_access_token");

        // WHEN
        LoginResponse response = authService.refreshToken(tokenStr);

        // THEN
        assertEquals("new_access_token", response.accessToken());
        assertEquals(tokenStr, response.refreshToken()); // Mantiene el mismo
    }

    @Test
    @DisplayName("Refresh Token - Expirado: Debe lanzar excepción y borrar el token")
    void refreshToken_Expired() {
        // GIVEN
        String tokenStr = "expired_token";
        RefreshToken rt = new RefreshToken();
        rt.setToken(tokenStr);
        rt.setExpiryDate(Instant.now().minusSeconds(10)); // YA EXPIRÓ

        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(rt));

        // WHEN & THEN
        assertThrows(TokenExpiredException.class, () -> authService.refreshToken(tokenStr));
        verify(refreshTokenRepository).delete(rt); // Debe limpiarlo de la BD
    }
}