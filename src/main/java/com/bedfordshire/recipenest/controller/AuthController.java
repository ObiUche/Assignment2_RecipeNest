package com.bedfordshire.recipenest.controller;


import com.bedfordshire.recipenest.dto.auth.*;
import com.bedfordshire.recipenest.service.AuthService;
import com.bedfordshire.recipenest.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * This is the restController for authentication
 * its job is to:
 * receives Http requests
 * validate the incoming DTO
 * call authhService
 * return HTTP Responses
 */

@RestController
@RequestMapping("/api/v1/auth") // Sets he base URL for every endpoint in class
public class AuthController {

    // Service that contains the real authentication business logic
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    /**
     *
     * @Valid
     * @RequestBody
     * Spring reads the json from @RequestBody
     * then converts it to RegisterRequest Record
     * then the input it validated against the DTO annotiations
     */

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        // Creates a new user, stores hashed password,
        // creates email verification token, and sends verification email
        authService.register(request);

        // Returns 200 ok with no body
        return ResponseEntity.ok().build();

    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        // Authenticates the user and returns access token + refreshToken
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request){
        // Accepts a refresh token and returns a new access token if valid
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);

    }


    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token){
        // Verifies the email verification token sent in the query parameter
        authService.verifyEmail(token);

        //Returns 200 OK if verification succeeds
        return ResponseEntity.ok().build();
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        // Generates password reset token and sends reset email
        authService.forgotPassword(request);

        // Returns 200 OK with no body
        return ResponseEntity.ok().build();

    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        // Validates reset token and updates the user's password
        authService.resetPassword(request);

        // Returns 200 Ok with no body
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request){
        // Uses the refresh token sent by client and revokes it in the database
        // so that token can no longer be used to request new access tokens
        authService.logout(request.refreshToken());

        // Returns 200 Ok with no response body after successful logout
        return ResponseEntity.ok().build();

    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request){
        // Use the email sent by the client to resent email
        authService.resendVerificationEmail(request);

        // Return 200 ok with no response after success sent
        return ResponseEntity.ok().build();
    }

}
