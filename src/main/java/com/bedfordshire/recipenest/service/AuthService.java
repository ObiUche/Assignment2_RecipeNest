package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.auth.*;
import com.bedfordshire.recipenest.entity.EmailVerificationToken;
import com.bedfordshire.recipenest.entity.PasswordResetToken;
import com.bedfordshire.recipenest.entity.RefreshToken;
import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.repository.EmailVerificationTokenRepository;
import com.bedfordshire.recipenest.repository.PasswordResetTokenRepository;
import com.bedfordshire.recipenest.repository.RefreshTokenRepository;
import com.bedfordshire.recipenest.repository.UserRepository;
import com.bedfordshire.recipenest.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Broad security lightweight controller
 * What ths file does:
 * this is the main auth business-logic class
 *  handles
 *  register
 *  login
 *  refreshToken
 *  verify email
 *  forgot password
 *  reset password
 *  logout
 */

@Service
@Transactional
public class AuthService {

    // Repository for user records
    private final UserRepository userRepository;

    // Repository for email verification tokens
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    // Repository for password reset tokens
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // Repository for refresh tokens
    private final RefreshTokenRepository refreshTokenRepository;

    // Used to hash passwords before saving them
    private final PasswordEncoder passwordEncoder;

    // Spring Security authentication entry point for login
    private final AuthenticationManager authenticationManager;

    // Used to generate JWT access tokens
    private final  JwtService jwtService;

    // Sends verification and rest emails
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailService emailService
    ){
        this.userRepository = userRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository  = passwordResetTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    // Creates the user, hashes password, create verification token. sends email
    public void register(RegisterRequest request){
        // Stop duplicate emails
        if(userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("Email is already in use");
        }

        // Fail fast and never allow self-register as admin
        if(request.role() == UserRole.ADMIN){
            throw new IllegalArgumentException("Admin role cannot be self-assigned");
        }

        // Create the new user
        User user = new User(
                request.firstname(),
                request.lastname(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        // Set the requested role (Public or Chef)
        user.setRole(request.role());

        // Email starts as unverified by default
        user.setEmailVerified(false);

        // Save user first so it gets an ID
        userRepository.save(user);

        // Crate email verification token
        EmailVerificationToken verificationToken = new EmailVerificationToken(user);
        emailVerificationTokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendEmailVerification(user, verificationToken.getToken());
    }

    // Let's spring security verify credentials and issues jwt access * refresh token
    public AuthResponse login(LoginRequest request) {
        // Ask Spring Security to authenticate using email + password
        // Find user by email
        User foundUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("No user found"));
        // Call authentication in try
        User authenticatedUser;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            // As User entity implements UserDetails
            // Spring returns your User as authenticated principal
            authenticatedUser = (User) authentication.getPrincipal();

            // Successful login should clear failed previous attempts
            authenticatedUser.resetFailedLoginAttempts();

            userRepository.save(authenticatedUser);

        } catch (BadCredentialsException e) {
            // Failed login increments the counter and may lock the account
            foundUser.incrementFailedLoginAttempts();
            userRepository.save(foundUser);
            throw e;
        }


        // As User entity implements UserDetails
        // Spring returns your User as authenticated principal


        // Extra safety check: block login if email not verified
        if (!authenticatedUser.isEmailVerified()) {
            throw new DisabledException("Please verify your email before logging in");
        }

        // Generate short-lived JWT access token
        String accessToken = jwtService.generateAccessToken(authenticatedUser);

        // Reuse existing refresh token row if present, otherwise create a new one
        RefreshToken refreshToken = refreshTokenRepository.findByUser(authenticatedUser)
                .map(existingToken -> {
                    existingToken.regenerate();
                    return existingToken;
                }).orElseGet(() -> new RefreshToken(authenticatedUser));
        refreshTokenRepository.save(refreshToken);

        // Return both tokens to the client
        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds(),
                authenticatedUser.getEmail(),
                authenticatedUser.getRole().name()
        );

    }

    // checks the DB refresh token then issues a new JWT
    public AuthResponse refresh(RefreshTokenRequest request){
        // Find the refresh token in the database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // Reject revoked or expired refresh tokens
        if(refreshToken.isExpired()){
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();


        // Create a fresh access token
        String accessToken = jwtService.generateAccessToken(user);


        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    // marks the account as verified
    public void verifyEmail(String tokenValue){
        // Find verification token from email link
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        // Reject expired token
        if(token.isExpired()){
            throw new IllegalArgumentException("Verification token has expired");
        }

        // Mark User as verified
        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Delete token after successful use
        emailVerificationTokenRepository.delete(token);

    }

    // Creates/regenerates a reset token and emails it to user
    public void forgotPassword(ForgotPasswordRequest request){

        // Look up the user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Reuse existing token row or create a new one
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                .map(existingToken -> {
                    existingToken.regenerate();
                    return  existingToken;
                })
                .orElseGet(() -> new PasswordResetToken(user));

        passwordResetTokenRepository.save(resetToken);

        // Send password reset email
        emailService.sendPasswordReset(user, resetToken.getToken());

    }


    // validate token and stores the new encoded password
    public void resetPassword(ResetPasswordRequest request){
        // Find token sent by user
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        // Reject expired token
        if(resetToken.isExpired()){
            throw new IllegalArgumentException("Reset token has expired");
        }

        User user = resetToken.getUser();

        // Save the new encoded password
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        // Clear lock state / failed attempts after successful password reset
        user.resetFailedLoginAttempts();

        userRepository.save(user);

        // Delete token after successful reset
        passwordResetTokenRepository.delete(resetToken);
    }




    // revokes the refresh token
    public void logout(String refreshTokenValue){
        // Look up refresh token and revoke it
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }


// Resend email verification email
    public void resendVerificationEmail(ResendVerificationRequest request){
        User foundUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));


        if(foundUser.isEmailVerified()){
            throw new IllegalArgumentException("User already verified");
        }

        EmailVerificationToken token = emailVerificationTokenRepository.findByUser(foundUser)
                        .map(existingToken -> {
                            // Reuse the existing database row and rotate the raw token value
                            // and expiry time before sending a fresh verifcation email
                            existingToken.regenerate();
                            return existingToken;
                        })
                                .orElseGet(() -> new EmailVerificationToken(foundUser));


        emailVerificationTokenRepository.save(token);

        emailService.sendEmailVerification(foundUser, token.getToken());

    }





}
