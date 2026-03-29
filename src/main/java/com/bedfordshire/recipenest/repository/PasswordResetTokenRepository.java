package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bedfordshire.recipenest.entity.User;
import java.util.Optional;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Finds a password reset token by the raw token string
    // Used when the user submits the token during password reset
    Optional<PasswordResetToken> findByToken(String token);

    // finds the current reset token for specific user
    // Needed if user requests another reset email
    Optional<PasswordResetToken> findByUser(User user);

    //Deletes the reset token for a specific user
    // Useful after the successful password reset
    void deleteByUser(User user);


}
