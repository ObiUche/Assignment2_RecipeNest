package com.bedfordshire.recipenest.dto.admin;

import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.entity.Recipe;

import java.time.LocalDateTime;

public record RecentRecipeResponse(

   // Recipe ID
   Long id,

   // Recipe title
   String title,

   // Recipe difficulty
   Difficulty difficulty,

   // Cuisine type, for example Nigerian or Italian
   String cuisineType,

   // Number of servins
   Integer servings,

   // Number of views
   Integer viewCount,

   // When the recipe was created
   LocalDateTime createDate,

   // Chef ID who owns the recipe
   Long chefId,

   // Chef full name
   String chefName
) {

    public static RecentRecipeResponse from(Recipe recipe){
        // Converts a recipe entity into a lightweight admin dashboard DTO
        return new RecentRecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDifficulty(),
                recipe.getCuisineType(),
                recipe.getServings(),
                recipe.getViewCount(),
                recipe.getCreatedDate(),
                recipe.getChef() != null ? recipe.getChef().getId() : null,
                recipe.getChef() != null ? recipe.getChef().getFullName() : null
        );
    }
}
