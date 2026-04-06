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
import com.bedfordshire.recipenest.entity.RecipePhoto;
import com.bedfordshire.recipenest.repository.RecipePhotoRepository;
import org.springframework.web.multipart.MultipartFile;
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

    private final S3Service s3Service;

    private final RecipePhotoRepository recipePhotoRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository,
                         S3Service s3Service,
                         RecipePhotoRepository recipePhotoRepository){
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
        this.recipePhotoRepository = recipePhotoRepository;
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

    public RecipeResponse  deleteRecipePhoto(Long recipeId, Long photoId, String currentUserEmail){

        // find the recipe
        Recipe currentRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe can not be found"));

        // Find the current user requesting the recipe
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User email is not found"));

        // Check if the user is authorized to complete this action
        assertCanModifyRecipe(currentRecipe, currentUser);

        // Find the exact photo belonging to this recipe
        RecipePhoto photoToDelete = currentRecipe.getPhotos()
                .stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));

        // Delete the physical file from S3 first so storage does not get orphaned
        if(photoToDelete.getImageUrl() != null && !photoToDelete.getImageUrl().isBlank()){
            s3Service.deleteFileByUrl(photoToDelete.getImageUrl());
        }

        // Remove the photo from the recipe
        // This helper also updates primary photo and mainImage when needed
        currentRecipe.removePhoto(photoToDelete);

        // Save once after the relationship change
        Recipe updatedRecipe = recipeRepository.save(currentRecipe);

        return  RecipeResponse.from(currentRecipe);

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

        // Loop through and delete every existing photo
        // recipe.getPhotos() returns List<RecipePhoto>
        recipe.getPhotos().forEach(photo ->
        {
            // Delete each uploaded s3 file before deleting recipe
            // so image files are not left orphaned in storage
            if(photo.getImageUrl() != null && !photo.getImageUrl().isBlank()){
                s3Service.deleteFileByUrl(photo.getImageUrl());
            }
        });
        // Delete the recipe
        recipeRepository.delete(recipe);

    }

    public RecipeResponse uploadRecipePhoto(Long recipeId, MultipartFile file, String currentUserEmail){

        // Find the recipe that will receive the uploaded photo
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Find the currently logged-in user from the email in the security context
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        // Reuse the same ownership and admin permission rules already used
        // for recipe updates and deletes so photo upload follows the same access police
        assertCanModifyRecipe(recipe , currentUser);

        // Upload the incoming image file to S3 and get back the public CloudFront URL
        String imageUrl = s3Service.uploadFile(file, "recipes");

        // create a new photo entity and attach it through the helper method
        // sets first image as primary
        // updates recipe.mainImage when needed
        RecipePhoto photo = new RecipePhoto(imageUrl, recipe);
        recipe.addPhoto(photo);

        // save the recipe so the new photo relation is persisted with recipe
        Recipe updatedRecipe = recipeRepository.save(recipe);

        // return the updated recipe response so the front gets the new image immediately
        return RecipeResponse.from(updatedRecipe);
    }

    public RecipeResponse replaceRecipePhoto(Long recipeId, Long photoId, MultipartFile file, String currentUserEmail){

        // Find the recipe that owns the photo to be replaced
        Recipe currentRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe can not be found"));

        // Find the user requesting the action
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        // Auth check to see if user can complete this action
        assertCanModifyRecipe(currentRecipe, currentUser);

        // Find the photo only from this recipe so users cannot replace
        // a photo belonging to a different recipe by passing another id
        RecipePhoto chosenPhoto = currentRecipe.getPhotos()
                .stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Recipe photo not found"));

        // Delete the previous S3 object first so old files do not remain orphaned
        if (chosenPhoto.getImageUrl() != null && !chosenPhoto.getImageUrl().isBlank()) {
            s3Service.deleteFileByUrl(chosenPhoto.getImageUrl());
        }


        // Upload the new file to the s3 bucket
       String newImageURL = s3Service.uploadFile(file, "recipes");

       // swap replace the old image url with new url
        chosenPhoto.setImageUrl(newImageURL);

        // if photo isPrimary update recipe photo
        if(chosenPhoto.isPrimary()){
            currentRecipe.setMainImage(newImageURL);
        }

        // Persist the changes
        Recipe updatedRecipe = recipeRepository.save(currentRecipe);

        recipePhotoRepository.save(chosenPhoto);

        return RecipeResponse.from(updatedRecipe);
    }

    public RecipeResponse setPrimaryRecipePhoto(Long recipeId, Long photoId, String currentUserEmail){

        // Find the recipe to alter
        Recipe currentRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Find the current user making a request
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User email not found"));

        // authentication check using the same access rules
        assertCanModifyRecipe(currentRecipe, currentUser);


        // Find the chosen photo in this recipe or fail if the ID does not belong to it
        RecipePhoto chosenPhoto = currentRecipe.getPhotos()
                .stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Photo Id has not been found"));

        // Clear the old primary flag for every photo before assigning the new one
        currentRecipe.getPhotos().forEach(photo -> photo.setPrimary(false));

        // Set the chosen photo to primary
        chosenPhoto.setPrimary(true);

        // Set the url image to new primary url
        currentRecipe.setMainImage(chosenPhoto.getImageUrl());

        // Save the updated recipe
        Recipe updatedRecipe = recipeRepository.save(currentRecipe);

        // Return the updated recipe
        return RecipeResponse.from(updatedRecipe);
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
