package com.bedfordshire.recipenest.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

        // The refresh token sent by the client
        // This is used to request a new access token
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
