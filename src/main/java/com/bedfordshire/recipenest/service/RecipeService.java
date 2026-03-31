package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.recipe.RecipeCreateRequest;
import com.bedfordshire.recipenest.dto.recipe.RecipeResponse;
import com.bedfordshire.recipenest.dto.recipe.RecipeUpdateRequest;
import com.bedfordshire.recipenest.entity.Recipe;
import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.repository.RecipeRepository;
import com.bedfordshire.recipenest.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class RecipeService {

    // Repository for recipe data
    private final RecipeRepository recipeRepository;

    // Repository for user data
    private final UserRepository userRepository;

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository){
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository ;
    }

    public RecipeResponse createRecipe(RecipeCreateRequest request , String currentUserEmail){
        // Find the current logged-in user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Only CHEF or ADMIN can create recipes
        if(currentUser.getRole() !=  UserRole.CHEF && currentUser.getRole() != UserRole.ADMIN){
            throw new AccessDeniedException("Only chefs and admins can create recipes");
        }

        // Build the recipe entity from the request DTO
        Recipe recipe = new Recipe();
        recipe.setTitle(request.title());
        recipe.setDescription(request.description());
        recipe.setInstructions(request.instructions());
        recipe.setCookingTime(request.cookingTime());
        recipe.setDifficulty(request.difficulty());
        recipe.setServings(request.servings());
        recipe.setCuisineType(request.cuisineType());
        recipe.setIngredients(request.Ingredients());
        recipe.setChef(currentUser);

        // Save and convert to response DTO
        Recipe savedRecipe = recipeRepository.save(recipe);
        return RecipeResponse.from(savedRecipe);
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> getAllRecipes(){

        // Return all recipes as responses DTO

        return recipeRepository.findAll()
                .stream()
                .map(RecipeResponse::from)
                .toList();

    }


    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long recipeId){
        // Find one recipe by ID
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
        return RecipeResponse.from(recipe);
    }

    public RecipeResponse updateRecipe(Long recipeId, RecipeUpdateRequest request, String currentUserEmail){
        // Find the recipe to update

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Find the currentUser
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if the user is allowed to modify this recipe
        assertCanModifyRecipe(recipe , currentUser);

        // Update the recipes fields
        recipe.setTitle(request.title()); // title
        recipe.setDescription(request.description()); // description
        recipe.setInstructions(request.instructions()); // instructions
        recipe.setCookingTime(request.cookingTime());// cooking time
        recipe.setDifficulty(request.difficulty());// diffculty
        recipe.setServings(request.servings());// servings
        recipe.setCuisineType(request.cuisineType()); // cusinestype
        recipe.setIngredients(request.Ingredients()); // ingrefients


        // Save updated recipe and convert to DTO
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return RecipeResponse.from(updatedRecipe);
    }

    public void deleteRecipe(Long recipeId , String currentUserEmail){
        // Find the recipe to delete
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Find the current user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if this user is allowed to delete this recipe
        assertCanModifyRecipe(recipe, currentUser);


        // Delete the recipe
        recipeRepository.delete(recipe);

    }


    private void assertCanModifyRecipe(Recipe recipe, User currentUser){
        // Admin can modify any recipe
        if(currentUser.getRole() == UserRole.ADMIN){
            return;
        }

        // Only chefs can modify recipes
        if(currentUser.getRole() != UserRole.CHEF){
            throw new AccessDeniedException("Only chefs can modify recipes");
        }

        // Chef can only modify their own recipe
        if(recipe.getChef() == null || !recipe.getChef().getId().equals(currentUser.getId())){
            throw new AccessDeniedException("You can only modify your own recipes");
        }
    }
}
