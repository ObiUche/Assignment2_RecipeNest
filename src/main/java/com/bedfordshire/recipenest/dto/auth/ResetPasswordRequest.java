package com.bedfordshire.recipenest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

   // Token sent the user's email
   // Backend uses this to find and validate the password reset
   @NotBlank(message = "Reset token is required")
   String token,

   // New raw password entered by the user
   // it will be encoded in the service layer
   @NotBlank(message = "New password is required")
   @Size(min = 8, max = 100, message = "Password must be between 8 and 100")
   String newPassword
) {}
