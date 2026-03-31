package com.bedfordshire.recipenest.dto.auth;

import com.bedfordshire.recipenest.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        // User's first name
        // Must not be blank and must be max 50 chars
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 charaters")
        String firstname,

        // User's last name
        // Must not be blank and must be at most 50 chars
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastname,

        // User's email address
        // Must not be blank and must be in valid email format
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        // Raw password from the client
        // Must not be blank and must be atleast 8 characters
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 chars")
        String password,

        // Requested role during registration
        // Service layer will aid this
        @NotNull(message = "Role is required")
        UserRole role


) { }
