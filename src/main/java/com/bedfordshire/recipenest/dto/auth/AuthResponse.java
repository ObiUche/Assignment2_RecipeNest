package com.bedfordshire.recipenest.dto.auth;

public record AuthResponse(

        // The signed JWT access token
        // This is sent in the authorization header for protected request
        String accessToken,

        // Long-lived token used to request a new access token
        // when the current access token expires.
        String refreshToken,

        // "Bearer"
        String tokenType,

        // How long the access token is valid for
        long expiresIn,

        // The logged=in user's email
        String email,

        // The logged-in user's role
        String role

) {
}
