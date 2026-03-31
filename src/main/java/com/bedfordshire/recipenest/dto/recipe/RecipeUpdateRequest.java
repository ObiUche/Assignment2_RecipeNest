package com.bedfordshire.recipenest.dto.recipe;


import com.bedfordshire.recipenest.entity.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeUpdateRequest(
        // Updated recipe title
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        // Updated short description
        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,


        // Updated full cooking instructions
        @NotBlank(message = "Instructions are required")
        @Size(max = 5000, message = "Instructions must not exceed 5000 characters")
        String instructions,

        // Updated cooking time in minutes
        @NotNull(message = "Cooking time is required")
        @Min(value = 1, message = "Cooking time must be atleast 1 minute")
        Integer cookingTime,


        // Updated difficulty level
        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        // Updated servings count
        @NotNull(message = "Servings is required")
        @Min(value = 1, message = "Servings must be atleast 1")
        Integer servings,


        // Updated cuisine Type
        @NotBlank(message = "Cuisine type is required")
        @Size(max = 100, message = "Cuisine type must not exceed 100 characters")
        String cuisineType,

        // Updated Ingredient List
        @NotEmpty(message = "At least one ingredient is required")
        List<@NotBlank(message = "Ingredient must not be blank") String> Ingredients
) {}
