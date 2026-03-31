package com.bedfordshire.recipenest.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
   // User's email address
   // Must not be blank and must be a valid email format
   @NotBlank(message = "Email is required")
   @Email(message = "Email must be valid")
   String email,

   // Raw password entered by the user
   // Must not be blank
   @NotBlank(message = "Password is required")
   String password
) {}
