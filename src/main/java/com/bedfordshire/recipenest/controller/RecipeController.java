package com.bedfordshire.recipenest.controller;


import com.bedfordshire.recipenest.dto.recipe.RecipeCreateRequest;
import com.bedfordshire.recipenest.dto.recipe.RecipeResponse;
import com.bedfordshire.recipenest.dto.recipe.RecipeUpdateRequest;
import com.bedfordshire.recipenest.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {


    // Service that contains the recipe business logic
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }


    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes(){
        // Public endpoints to fetch all recipes
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id){
        // public endpoint to fetch a single recipe by ID
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('CHEF', 'ADMIN')")
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody RecipeCreateRequest request,
            Authentication authentication
            ){
        // authentiation.getName() returns the current user's email
        RecipeResponse response = recipeService.createRecipe(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF','ADMIN')")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeUpdateRequest request,
            Authentication authentication){
        // Service layer will also check recipe ownerhsip
        RecipeResponse response = recipeService.updateRecipe(id,request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF', 'ADMIN')")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            Authentication authentication)
    {
        // Service Layer will also chek recipe ownership
        recipeService.deleteRecipe(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
