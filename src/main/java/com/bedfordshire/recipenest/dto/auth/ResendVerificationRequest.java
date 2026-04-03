package com.bedfordshire.recipenest.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationRequest(

        // Email must be not blank and valid email
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {
}
