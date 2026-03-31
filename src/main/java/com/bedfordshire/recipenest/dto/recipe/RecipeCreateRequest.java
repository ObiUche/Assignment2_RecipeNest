package com.bedfordshire.recipenest.dto.recipe;


import com.bedfordshire.recipenest.entity.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public record RecipeCreateRequest(
        // Recipe title shown to users
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 Characters")
        String title,

        // Short summary of the recipe
        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,


        // Full Cooking instructions
        @NotBlank(message = "Instructions are required")
        @Size(max = 5000, message = "Instructions must not exceed 5000 charaters")
        String instructions,

        // Time to cook in minutes
        @NotNull(message = "Cooking time is required")
        @Min(value = 1, message = "Cooking time must be atleast 1 minute")
        Integer cookingTime,

        // Difficulty enum: EASY, MEDIUM AND HARD
        @NotNull(message = "Diffculty is required")
        Difficulty difficulty,

        // Number of people the recipe serves
        @NotNull(message = "Servings is required")
        @Min(value =1 , message =  "Servings must be atleast 1")
        Integer servings,

        // Cuisine type such as English, Nigerian , Indian
        @NotBlank(message = "Cuisine type is required")
        @Size(max = 100, message = "Cuisine type must not exceeed 100 characters")
        String cuisineType,

        // List of ingredients used in recipe
        @NotEmpty(message = "At least one ingredient is required")
        List<@NotBlank(message = "Ingredient must not be blank") String> Ingredients



) {}
