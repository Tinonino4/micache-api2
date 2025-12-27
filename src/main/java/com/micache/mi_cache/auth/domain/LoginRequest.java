package com.micache.mi_cache.auth.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesión")
public record LoginRequest(

        @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @Schema(description = "Contraseña del usuario", example = "S3cr3tP@ss!")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
