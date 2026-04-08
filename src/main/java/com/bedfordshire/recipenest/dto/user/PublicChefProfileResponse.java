package com.bedfordshire.recipenest.dto.user;

import com.bedfordshire.recipenest.entity.User;

public record PublicChefProfileResponse(

        // Public id of the chef
        Long id,

        // Public first name
        String firstName,

        // Public last name
        String lastName,

        // Full name for display
        String fullName,

        // Public chef profile image
        String profilePhoto,

        // Public location
        String location,

        // Public bio
        String bio,

        // Public cuisine speciality
        String cuisineSpeciality

) {
    public static PublicChefProfileResponse from(User user){
        // Converts a User Entity into a public chef profile response DTO
        return new PublicChefProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getProfilePhoto(),
                user.getLocation(),
                user.getBio(),
                user.getCuisineSpeciality()
        );
    }
}