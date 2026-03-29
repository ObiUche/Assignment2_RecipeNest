package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // find user but email address
    // used during the login
    Optional<User> findByEmail(String email);

    // checks if email already exists in the db
    // used during registration
    boolean existsByEmail(String email);
}
