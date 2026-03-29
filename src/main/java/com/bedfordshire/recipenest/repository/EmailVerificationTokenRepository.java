package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.EmailVerificationToken;
import com.bedfordshire.recipenest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    // Finds a verification token by the raw string
    // used when the user clicks the email verification link
    Optional<EmailVerificationToken> findByToken(String token);


    // Find the current verification token for a specific user
    // helps when resending verification emails
    Optional<EmailVerificationToken> findByUser(User user);

    // Deletes a users verification token
    // Useful after a successful verification
    void deleteByUser(User user);

}
