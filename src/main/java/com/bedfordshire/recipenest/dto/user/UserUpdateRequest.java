package com.bedfordshire.recipenest.dto.user;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        // Updated first name
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        // Updated last name
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        // Updated user location
        @Size(max = 100, message = "Location must not exceed 100 characters")
        String location,

        // Updated short biography
        @Size(max = 1000, message = "Bio must not exceed 1000 characters")
        String bio,

        // Updated cuisine speciality
        @Size(max = 100, message = "Cuisine speciality must not exceed 100 characters")
        String cuisineSpeciality

) {
}