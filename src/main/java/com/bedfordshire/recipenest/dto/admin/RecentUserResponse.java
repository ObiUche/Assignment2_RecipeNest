package com.bedfordshire.recipenest.dto.admin;

import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;

import java.time.LocalDateTime;

public record RecentUserResponse(
        // User ID
        Long id,

        // User's first name
        String firstName,

        // User's last name
        String lastName,

        // User's email address
        String email,

        // User's role in the system
        UserRole role,

        // Whether the user's email has been verified
        boolean emailVerified,

        // Whether the account is active
        boolean active,

        // When the user joined
        LocalDateTime joinDate
) {

    public static RecentUserResponse from(User user){
        // Converts a User entity into a lightweight admin dashboard DTO
        return new RecentUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified(),
                user.isActive(),
                user.getJoinDate()
        );
    }
}
