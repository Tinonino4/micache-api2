package com.micache.mi_cache.auth.domain;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}