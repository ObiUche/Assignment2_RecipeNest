package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.Recipe;
import com.bedfordshire.recipenest.entity.RecipePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipePhotoRepository extends JpaRepository<RecipePhoto, Long> {
    // will be used by recipeService
    // Find all stored photos by recipe
    List<RecipePhoto> findAllByRecipe(Recipe recipe);


    // Find primary photo by recipe
    // this helps when showing a cover image on recipe cars or detail pages
    Optional<RecipePhoto> findByRecipeAndIsPrimaryTrue(Recipe recipe);


    // Delete all photos by recipe
    // useful if a recipe is deleted and its related photo metadata must also be removed
    void deleteAllByRecipe(Recipe recipe);
}
