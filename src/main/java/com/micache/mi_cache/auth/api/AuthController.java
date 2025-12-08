package com.micache.mi_cache.auth.api;

import com.micache.mi_cache.auth.domain.*;
import com.micache.mi_cache.security.exception.InvalidTokenException;
import com.micache.mi_cache.security.exception.TokenExpiredException;
import com.micache.mi_cache.security.service.ConfirmationTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.micache.mi_cache.auth.application.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro, login y confirmación de email")
public class AuthController {

    private final AuthService authService;
    private final ConfirmationTokenService confirmationTokenService;

    public AuthController(AuthService authService, ConfirmationTokenService confirmationTokenService) {
        this.authService = authService;
        this.confirmationTokenService = confirmationTokenService;
    }

    @PostMapping("/google")
    @Operation(summary = "Login con Google", description = "Autentica o registra un usuario mediante Google ID Token")
    public ResponseEntity<LoginResponse> googleLogin(@Valid @RequestBody SocialLoginRequest request) {
        LoginResponse response = authService.loginWithGoogle(request.token());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario registrado y devuelve un token JWT para futuras solicitudes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, JWT devuelto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request.email(), request.password());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar Access Token", description = "Obtiene un nuevo JWT usando un Refresh Token válido")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario, almacena su información y envía un correo electrónico de confirmación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación o datos duplicados")
    })
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm")
    @Operation(summary = "Confirmar email", description = "Confirma un correo electrónico usando el token enviado en el email tras el registro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmado correctamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    public ResponseEntity<String> confirm(@RequestParam String token) {
        try {
            confirmationTokenService.validateToken(token); // lanza excepciones custom
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:8082/email-confirmed"))
                    .build();
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:8082/email-error?reason=expired"))
                    .build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:8082/email-error?reason=invalid"))
                    .build();
        }
    }
}
