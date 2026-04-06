package com.bedfordshire.recipenest.controller;


import com.bedfordshire.recipenest.dto.recipe.RecipeCreateRequest;
import com.bedfordshire.recipenest.dto.recipe.RecipeResponse;
import com.bedfordshire.recipenest.dto.recipe.RecipeUpdateRequest;
import com.bedfordshire.recipenest.service.RecipeService;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasAnyRole('CHEF','ADMIN')")
    public ResponseEntity<RecipeResponse> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ){
        // Upload the incoming recipe photo and return the updated recipe DTO
        // so the frontend can immediately show the new main image if needed
       RecipeResponse response =  recipeService.uploadRecipePhoto(id,file, authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF','ADMIN')")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeUpdateRequest request,
            Authentication authentication){
        // Service layer will also check recipe ownership
        RecipeResponse response = recipeService.updateRecipe(id,request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{recipeId}/photos/{photoId}")
    @PreAuthorize("hasAnyRole('CHEF','ADMIN')")
    public ResponseEntity<RecipeResponse> replaceRecipePhoto(
            @PathVariable Long recipeId,
            @PathVariable Long photoId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ){
        // Replace one existing recipe photo with a new uploaded file
        // return the updated recipe so the frontend gets a new image
        RecipeResponse response = recipeService.replaceRecipePhoto(
                recipeId,
                photoId,
                file,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }


    /**
     *
     * @PutMapping = replace/update a full resource
     * @PatchMapping = update just on part of the resource
     */
    @PatchMapping("/{recipeId}/photos/{photoId}/primary")
    @PreAuthorize("hasAnyRole('CHEF','ADMIN')")
    public ResponseEntity<RecipeResponse> setPrimaryRecipePhoto(
            @PathVariable Long recipeId,
            @PathVariable Long photoId,
            Authentication authentication
    ){
        // Set one recipe photo as the primary image
        // and return the updated recipe to the client
        RecipeResponse response = recipeService.setPrimaryRecipePhoto(
                recipeId,
                photoId,
                authentication.getName()
        );

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

    @DeleteMapping("/{recipeId}/photos/{photoId}")
    @PreAuthorize("hasAnyRole('CHEF', 'ADMIN')")
    public ResponseEntity<RecipeResponse> deleteRecipePhoto(
            @PathVariable Long recipeId,
            @PathVariable Long photoId,
            Authentication authentication
    ){
        // delete one recipe photo, update the image if needed,
        // and return the updated recipe back to the client
        RecipeResponse response = recipeService.deleteRecipePhoto(
                recipeId,
                photoId,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }


}
