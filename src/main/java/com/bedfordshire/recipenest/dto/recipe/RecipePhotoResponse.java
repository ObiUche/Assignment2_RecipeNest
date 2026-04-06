package com.bedfordshire.recipenest.dto.recipe;

import com.bedfordshire.recipenest.entity.RecipePhoto;

public record RecipePhotoResponse(
        Long id,
        String imageUrl,
        String caption,
        boolean isPrimary
) {
     public static RecipePhotoResponse from(RecipePhoto photo){

         // Converts a RecipePhoto Entity to a smaller RecipePhotoResponse DTO
         // so the API returns photo data without exposing entity directly
         return new RecipePhotoResponse(
                 photo.getId(),
                 photo.getImageUrl(),
                 photo.getCaption(),
                 photo.isPrimary()
         );
    }
}
