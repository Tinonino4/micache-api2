package com.micache.mi_cache.controller;

import com.micache.mi_cache.dto.RegisterRequest;
import com.micache.mi_cache.exception.InvalidTokenException;
import com.micache.mi_cache.exception.TokenExpiredException;
import com.micache.mi_cache.service.ConfirmationTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.micache.mi_cache.dto.LoginRequest;
import com.micache.mi_cache.dto.LoginResponse;
import com.micache.mi_cache.model.User;
import com.micache.mi_cache.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y confirmación de email")
public class AuthController {

    private final AuthService authService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario registrado y devuelve un token JWT para futuras solicitudes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, JWT devuelto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
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
                    .location(URI.create("http://localhost:8081/email-confirmed"))
                    .build();
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:8081/email-error?reason=expired"))
                    .build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:8081/email-error?reason=invalid"))
                    .build();
        }
    }
}
