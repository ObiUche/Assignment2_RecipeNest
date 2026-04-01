package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.User;

import com.bedfordshire.recipenest.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // find user but email address
    // used during the login
    Optional<User> findByEmail(String email);

    // checks if email already exists in the db
    // used during registration
    boolean existsByEmail(String email);

    // Counts how many users have a specific role
    // Used by admin dashboard for total chefs , users, and total admin
    long countByRole(UserRole role);

    // Count how many users have verified their email.
    // Used for admin dashboard verified-user statistics
    long countByEmailVerifiedTrue();

    // Count how many users haven't verified email
    long countByEmailVerifiedFalse();

    // Count how many user are currently locked.
    // Used for admin dashboard locked-account stats
    long countByAccountNonLockedFalse();

}
