package com.micache.mi_cache.auth.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticación exitosa")
public record LoginResponse(

        @Schema(description = "Token JWT para acceder a recursos protegidos")
        String accessToken,

        @Schema(description = "Token para renovar el acceso cuando el JWT expire")
        String refreshToken
) {}