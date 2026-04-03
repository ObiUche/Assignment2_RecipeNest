package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.auth.AuthResponse;
import com.bedfordshire.recipenest.dto.auth.ForgotPasswordRequest;
import com.bedfordshire.recipenest.dto.auth.LoginRequest;
import com.bedfordshire.recipenest.dto.auth.RefreshTokenRequest;
import com.bedfordshire.recipenest.dto.auth.RegisterRequest;
import com.bedfordshire.recipenest.dto.auth.ResetPasswordRequest;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Register creates user, verification token, and sends email")
    void register_validRequest_saveUserTokenAndSendsEmail(){
        RegisterRequest request = new RegisterRequest(
                "Obinna",
                "Uche",
                "obinna@example.com",
                "password123",
                UserRole.PUBLIC
        );

        when(userRepository.existsByEmail("obinna@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo("Obinna");
        assertThat(savedUser.getLastName()).isEqualTo("Uche");
        assertThat(savedUser.getEmail()).isEqualTo("obinna@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.PUBLIC);
        assertThat(savedUser.isEmailVerified()).isFalse();

        ArgumentCaptor<EmailVerificationToken> tokenCaptor =
                ArgumentCaptor.forClass(EmailVerificationToken.class);
        verify(emailVerificationTokenRepository).save(tokenCaptor.capture());

        EmailVerificationToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUser()).isSameAs(savedUser);
        assertThat(savedToken.getToken()).isNotBlank();

        verify(emailService).sendEmailVerification(savedUser, savedToken.getToken());

    }

    @Test
    @DisplayName("Register with duplicate email throws exception")
    void register_duplicateEmail_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "Obinna",
                "Uche",
                "obinna@example.com",
                "password123",
                UserRole.PUBLIC
        );

        when(userRepository.existsByEmail("obinna@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Email is already in use");
        verify(userRepository, never()).save(any());
        verify(emailVerificationTokenRepository, never()).save(any());
        verify(emailService, never()).sendEmailVerification(any(), anyString());
    }

    @Test
    @DisplayName("Register with admin role throws exception")
    void register_adminRole_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "Obinna",
                "Uche",
                "obinna@example.com",
                "password123",
                UserRole.ADMIN
        );

        when(userRepository.existsByEmail("obinna@example.com")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Admin role cannot be self-assigned");
        verify(userRepository, never()).save(any());
        verify(emailVerificationTokenRepository, never()).save(any());
        verify(emailService, never()).sendEmailVerification(any(), anyString());
    }

    @Test
    @DisplayName("Login returns access token and existing refresh token when user is verified")
    void login_verifiedUserWithExistingRefreshToken_returnsAuthResponse() {
        LoginRequest request = new LoginRequest("obinna@example.com", "password123");

        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        user.setId(1L);
        user.setRole(UserRole.PUBLIC);
        user.setEmailVerified(true);

        RefreshToken refreshToken = new RefreshToken(user);
        String oldTokenValue = refreshToken.getToken();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token-123");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(86400L);
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(refreshToken));

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token-123");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotEqualTo(oldTokenValue);
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(86400L);
        assertThat(response.email()).isEqualTo("obinna@example.com");
        assertThat(response.role()).isEqualTo("PUBLIC");

        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    @DisplayName("Login creates refresh token when user does not already have one")
    void login_verifiedUserWithoutExistingRefreshToken_createsOne() {
        LoginRequest request = new LoginRequest("obinna@example.com", "password123");

        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        user.setId(1L);
        user.setRole(UserRole.CHEF);
        user.setEmailVerified(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token-123");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(86400L);
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token-123");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(86400L);
        assertThat(response.email()).isEqualTo("obinna@example.com");
        assertThat(response.role()).isEqualTo("CHEF");

        ArgumentCaptor<RefreshToken> refreshTokenCaptor =
                ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken savedRefreshToken = refreshTokenCaptor.getValue();
        assertThat(savedRefreshToken.getUser()).isSameAs(user);
        assertThat(savedRefreshToken.getToken()).isNotBlank();
    }

    @Test
    @DisplayName("Login with unverified user throws exception")
    void login_unverifiedUser_throwsException() {
        LoginRequest request = new LoginRequest("obinna@example.com", "password123");

        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        user.setEmailVerified(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        DisabledException exception = assertThrows(
                DisabledException.class,
                () -> authService.login(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Please verify your email before logging in");
        verify(jwtService, never()).generateAccessToken(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Refresh returns new access token for valid refresh token")
    void refresh_validToken_returnsAuthResponse() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        user.setId(1L);
        user.setRole(UserRole.PUBLIC);

        RefreshToken refreshToken = new RefreshToken(user);

        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken.getToken());

        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(86400L);

        AuthResponse response = authService.refresh(request);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo(refreshToken.getToken());
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(86400L);
        assertThat(response.email()).isEqualTo("obinna@example.com");
        assertThat(response.role()).isEqualTo("PUBLIC");
    }

    @Test
    @DisplayName("Refresh with missing token throws exception")
    void refresh_missingToken_throwsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("missing-token");

        when(refreshTokenRepository.findByToken("missing-token")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.refresh(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Invalid refresh token");
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Refresh with expired token throws exception")
    void refresh_expiredToken_throwsException() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        RefreshToken refreshToken = new RefreshToken(user);
        refreshToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken.getToken());

        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.refresh(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Refresh token is expired or revoked");
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Verify email marks user verified and deletes token")
    void verifyEmail_validToken_verifiesUserAndDeletesToken() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        user.setEmailVerified(false);

        EmailVerificationToken token = new EmailVerificationToken(user);
        token.setToken("verify-token");

        when(emailVerificationTokenRepository.findByToken("verify-token"))
                .thenReturn(Optional.of(token));

        authService.verifyEmail("verify-token");

        assertThat(user.isEmailVerified()).isTrue();
        verify(userRepository).save(user);
        verify(emailVerificationTokenRepository).delete(token);
    }

    @Test
    @DisplayName("Verify email with invalid token throws exception")
    void verifyEmail_invalidToken_throwsException() {
        when(emailVerificationTokenRepository.findByToken("bad-token"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.verifyEmail("bad-token")
        );

        assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
        verify(userRepository, never()).save(any());
        verify(emailVerificationTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Verify email with expired token throws exception")
    void verifyEmail_expiredToken_throwsException() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");

        EmailVerificationToken token = new EmailVerificationToken(user);
        token.setToken("expired-token");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(emailVerificationTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(token));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.verifyEmail("expired-token")
        );

        assertThat(exception.getMessage()).isEqualTo("Verification token has expired");
        verify(userRepository, never()).save(any());
        verify(emailVerificationTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Forgot password creates token and sends email")
    void forgotPassword_existingUser_createsTokenAndSendsEmail() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");

        ForgotPasswordRequest request = new ForgotPasswordRequest("obinna@example.com");

        when(userRepository.findByEmail("obinna@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        authService.forgotPassword(request);

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUser()).isSameAs(user);
        assertThat(savedToken.getToken()).isNotBlank();

        verify(emailService).sendPasswordReset(user, savedToken.getToken());
    }

    @Test
    @DisplayName("Forgot password regenerates existing token")
    void forgotPassword_existingToken_regeneratesAndSendsEmail() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        PasswordResetToken existingToken = new PasswordResetToken(user);
        String oldTokenValue = existingToken.getToken();

        ForgotPasswordRequest request = new ForgotPasswordRequest("obinna@example.com");

        when(userRepository.findByEmail("obinna@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.of(existingToken));

        authService.forgotPassword(request);

        assertThat(existingToken.getToken()).isNotEqualTo(oldTokenValue);
        verify(passwordResetTokenRepository).save(existingToken);
        verify(emailService).sendPasswordReset(user, existingToken.getToken());
    }

    @Test
    @DisplayName("Forgot password with missing user throws exception")
    void forgotPassword_missingUser_throwsException() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("missing@example.com");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> authService.forgotPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordReset(any(), anyString());
    }

    @Test
    @DisplayName("Reset password updates password, unlocks account, and deletes token")
    void resetPassword_validToken_updatesPasswordUnlocksUserAndDeletesToken() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "old-password");
        user.setFailedLoginAttempts(5);
        user.setAccountNonLocked(false);
        user.setLockoutTime(LocalDateTime.now().plusMinutes(10));

        PasswordResetToken token = new PasswordResetToken(user);
        token.setToken("reset-token");

        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword123");

        when(passwordResetTokenRepository.findByToken("reset-token"))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded-new-password");

        authService.resetPassword(request);

        assertThat(user.getPasswordHash()).isEqualTo("encoded-new-password");
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.getLockoutTime()).isNull();

        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    @DisplayName("Reset password with invalid token throws exception")
    void resetPassword_invalidToken_throwsException() {
        ResetPasswordRequest request = new ResetPasswordRequest("bad-token", "newPassword123");

        when(passwordResetTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Invalid reset token");
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Reset password with expired token throws exception")
    void resetPassword_expiredToken_throwsException() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "old-password");

        PasswordResetToken token = new PasswordResetToken(user);
        token.setToken("expired-reset-token");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        ResetPasswordRequest request =
                new ResetPasswordRequest("expired-reset-token", "newPassword123");

        when(passwordResetTokenRepository.findByToken("expired-reset-token"))
                .thenReturn(Optional.of(token));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Reset token has expired");
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Logout revokes refresh token")
    void logout_validToken_revokesAndSavesToken() {
        User user = new User("Obinna", "Uche", "obinna@example.com", "hashed-password");
        RefreshToken refreshToken = new RefreshToken(user);

        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));

        authService.logout(refreshToken.getToken());

        assertThat(refreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    @DisplayName("Logout with invalid token throws exception")
    void logout_invalidToken_throwsException() {
        when(refreshTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.logout("bad-token")
        );

        assertThat(exception.getMessage()).isEqualTo("Invalid refresh token");
        verify(refreshTokenRepository, never()).save(any());
    }

}
