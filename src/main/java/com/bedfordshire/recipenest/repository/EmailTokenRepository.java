package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
}
