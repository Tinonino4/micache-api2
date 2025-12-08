package com.micache.mi_cache.auth.domain;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "El token es obligatorio")
        String token
) {}
