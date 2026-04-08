package com.bedfordshire.recipenest.dto.user;

import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;

import java.time.LocalDateTime;

public record UserResponse(

        // Database Id of the user
        Long id,

        // Users first name
        String firstName,

        // Users last name
        String lastName,

        // Users email address
        String email,

        // Profile photo url
        String profilePhoto,

        // User location
        String location,

        // User bio text
        String bio,

        // Users main cooking speciality
        String cuisineSpeciality,

        // Current system role for the user
        UserRole role,

        // Date the user joined the platform
        LocalDateTime joinDate

) {
    public static UserResponse from(User user){
        // Converts a User Entity into UserResponse DTO
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfilePhoto(),
                user.getLocation(),
                user.getBio(),
                user.getCuisineSpeciality(),
                user.getRole(),
                user.getJoinDate()
        );
    }
}