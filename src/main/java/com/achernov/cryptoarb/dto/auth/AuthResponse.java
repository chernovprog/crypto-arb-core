package com.achernov.cryptoarb.dto.auth;

public record AuthResponse(
        Long id,
        String name,
        String email
) {}
