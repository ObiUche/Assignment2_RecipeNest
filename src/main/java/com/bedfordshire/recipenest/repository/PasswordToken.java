package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordToken extends JpaRepository<PasswordResetToken, Long> {
}
