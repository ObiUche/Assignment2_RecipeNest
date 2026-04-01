package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.recipe.RecipeCreateRequest;
import com.bedfordshire.recipenest.dto.recipe.RecipeResponse;
import com.bedfordshire.recipenest.dto.recipe.RecipeUpdateRequest;
import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.entity.Recipe;
import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.repository.RecipeRepository;
import com.bedfordshire.recipenest.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    // Mock repositories so we only test service logic
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    // Inject mocks into the service under the test
    @InjectMocks
    private RecipeService recipeService;

    private User chefUser;
    private User otherChefUser;
    private User publicUser;
    private User adminUser;
    private Recipe recipe;

    @BeforeEach
    void setUp(){
        // Create a chef who owns the recipe
        chefUser = new User(
                "Obinna",
                "Uche",
                "chef@example.com",
                "encoded-password");
        chefUser.setId(1L);
        chefUser.setRole(UserRole.CHEF);
        chefUser.setEmailVerified(true);

        // Create another chef for ownership
        otherChefUser = new User(
                "Amara",
                "Cook",
                "otherchef@example.com",
                "encoded-password"
        );
        otherChefUser.setId(2L);
        otherChefUser.setRole(UserRole.CHEF);
        otherChefUser.setEmailVerified(true);

        // Create a public user who should not be allowed to create recipes
        publicUser = new User(
                "Sarah",
                "Viewer",
                "public@example.com",
                "encoded-password"
        );
        publicUser.setId(3L);
        publicUser.setRole(UserRole.PUBLIC);
        publicUser.setEmailVerified(true);

        // Create an admin user who can modify any recipe
        adminUser = new User(
                "Admim",
                "User",
                "admin@example.com",
                "encoded-password"
        );
        adminUser.setId(4L);
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setEmailVerified(true);

        // Create a sample recipe owned by chefUser
        recipe = new Recipe();
        recipe.setId(10L);
        recipe.setTitle("Jollof Rice");
        recipe.setDescription("Spicy rice dish");
        recipe.setInstructions("Cook properly");
        recipe.setCookingTime(60);
        recipe.setDifficulty(Difficulty.MEDIUM);
        recipe.setServings(4);
        recipe.setCuisineType("Nigerian");
        recipe.setIngredients(List.of("Rice", "Tomatoes", "Onion"));
        recipe.setViewCount(5);
        recipe.setCreatedDate(LocalDateTime.now());
        recipe.setChef(chefUser);

    }

    @Test
    void createRecipe_shouldCreateRecipeForChef() {
        // Build request DTO
        RecipeCreateRequest request = new RecipeCreateRequest(
                "Fried Rice",
                "Classic fried rice",
                "Cook rice and stir fry ingredients",
                45,
                Difficulty.EASY,
                3,
                "Nigerian",
                List.of("Rice", "Carrots", "Peas")
        );

        // Current logged-in user is chef
        when(userRepository.findByEmail("chef@example.com")).thenReturn(Optional.of(chefUser));

        // Return the saved recipe object back rom the mock repository
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(
                invocation -> {
                    Recipe savedRecipe = invocation.getArgument(0);
                    savedRecipe.setId(99L);
                    savedRecipe.setCreatedDate(LocalDateTime.now());
                    return savedRecipe;
                });

        // Execute service method
        RecipeResponse response = recipeService.createRecipe(request, "chef@example.com");

        // Assert mapped response values
        assertEquals(99L, response.id());
        assertEquals("Fried Rice", response.title());
        assertEquals(Difficulty.EASY, response.difficulty());
        assertEquals("Nigerian", response.cuisineType());
        assertEquals(chefUser.getId(), response.chefId());
    }

    @Test
    void createRecipe_shouldRejectPublicUser(){
        // Build request DTO
        RecipeCreateRequest request = new RecipeCreateRequest(
                "Fried Rice",
                "Classic fried rice",
                "Cook rice and stir fry ingredients",
                45,
                Difficulty.EASY,
                3,
                "Nigerian",
                List.of("Rice", "Carrots", "Peas" )
        );

        // Current logged-in user is PUBLIC
        when(userRepository.findByEmail("public@example.com")).thenReturn(Optional.of(publicUser));

        // Public users must not be able to create recipes
        assertThrows(
                AccessDeniedException.class,
                () -> recipeService.createRecipe(request, "public@example.com")
        );
    }

    @Test
    void updateRecipe_shouldAllowOwnerChef(){
        // Build update request DTO
        RecipeUpdateRequest request = new RecipeUpdateRequest(
                "Updated Jollof Rice",
                "Updated description",
                "Updated instructions",
                70,
                Difficulty.HARD,
                5,
                "Nigerian",
                List.of("Rice", "Tomatoes", "Pepper")
        );

        // Mock recipe lookup and current user lookup
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(userRepository.findByEmail("chef@example.com")).thenReturn(Optional.of(chefUser));

        // Return updated recipe from repository
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        // Execute update
        RecipeResponse response = recipeService.updateRecipe(10L,request,"chef@example.com");

        // Assert updated values
        assertEquals("Updated Jollof Rice", response.title());
        assertEquals(Difficulty.HARD, response.difficulty());
        assertEquals(5, response.servings());
    }

    @Test
    void updateRecipe_shouldRejectDifferentChef(){
        // Build update request DTO
        RecipeUpdateRequest request = new RecipeUpdateRequest(
                "Updated Jollof Rice",
                "Updated description",
                "Updated instructions",
                70,
                Difficulty.HARD,
                5,
                "Nigerian",
                List.of("Rice", "Tomatoes", "Pepper")
        );

        // Recipe belongs to chefUser, but otherChefUser tries to update and should fail
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(userRepository.findByEmail("otherchef@example.com")).thenReturn(Optional.of(otherChefUser));

        // Non-owner chef must be denied
        assertThrows(
                AccessDeniedException.class,
                () -> recipeService.updateRecipe(10L,request,"otherchef@example.com")
        );
    }

    @Test
    void deleteRecipe_shouldAllowAdmin(){
        //Recipe exists and admin is logged in
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Admin should be allowed to delete any recipe
        assertDoesNotThrow(() -> recipeService.deleteRecipe(10L, "admin@example.com"));
    }

    @Test
    void getRecipeById_shouldThrowWhenRecipeDoesNotExist(){
        // Mock missing recipe
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        // Service should throw if recipe is missing
        assertThrows(
                EntityNotFoundException.class,
                () -> recipeService.getRecipeById(999L)
        );
    }
}
