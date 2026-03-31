package com.bedfordshire.recipenest.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record ForgotPasswordRequest(

        // Email address of the user requesting a password reset
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {
}
