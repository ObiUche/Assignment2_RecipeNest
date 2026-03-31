package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.RefreshToken;
import com.bedfordshire.recipenest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Finds a refresh token using the raw token string
    // Used when the client sends a refresh token to request a new token
    Optional<RefreshToken> findByToken(String token);

    // Find the refresh token for a specific user
    // Used to reuse or regenerate one token record
    Optional<RefreshToken> findByUser(User user);
}
