package com.bedfordshire.recipenest.dto.recipe;
import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.entity.Recipe;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeResponse(

        // Database ID of the recipe
        Long id,

        // Recipe Title
        String title,

        // Short description of the recipe
        String description,

        // Full cooking instructions
        String instructions,

        // Cooking time in minutes
        Integer cookingTime,

        // Difficulty level as Text
        Difficulty difficulty,

        // Number of servings
        Integer servings,

        // Cuisine type
        String cuisineType,

        // Main image URL for recipe
        String mainImage,

        // Number of times the recipe has been viewed
        Integer viewCount,

        // When the recipe was created
        LocalDateTime createdDate,

        // Id of the chef who owns the recipe
        Long chefId,


        // Full name of the chef who owns the recipe
        String chefName,

        // Ingredients List
        List<String> ingredients,

        // All uploaded photos linked to this recipe, including primary status
        List<RecipePhotoResponse> photos

) {
    public static RecipeResponse from(Recipe recipe){
        // Converts a Recipe Entity  into RecipeResponse DTO
        return new RecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getInstructions(),
                recipe.getCookingTime(),
                recipe.getDifficulty(),
                recipe.getServings(),
                recipe.getCuisineType(),
                recipe.getMainImage(),
                recipe.getViewCount(),
                recipe.getCreatedDate(),
                recipe.getChef() != null ? recipe.getChef().getId() : null,
                recipe.getChef() != null ? recipe.getChef().getFullName() : null,
                recipe.getIngredients(),
                recipe.getPhotos().stream()
                        .map(RecipePhotoResponse :: from)
                        .toList()

        );
    }
}