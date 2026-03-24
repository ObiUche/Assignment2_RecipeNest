package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
