package com.micache.mi_cache.auth.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un nuevo usuario")
public record RegisterRequest(

        @Schema(description = "Email único del usuario", example = "nuevo@micache.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Contraseña segura (Min 8 chars, 1 mayús, 1 minús, 1 número)", example = "StrongP@ss123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must have at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter and one number"
        )
        String password,

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
        @NotBlank(message = "Name is required")
        String name
) {}