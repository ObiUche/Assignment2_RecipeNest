package com.bedfordshire.recipenest.controller;

import com.bedfordshire.recipenest.dto.recipe.RecipeCreateRequest;
import com.bedfordshire.recipenest.dto.recipe.RecipeResponse;
import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.bedfordshire.recipenest.dto.recipe.RecipeUpdateRequest;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

// GET TEST
    @Test
    void getAllRecipes_shouldReturnRecipeList() throws Exception {

        // Arrange: mock service response
        List<RecipeResponse> recipes = List.of(
                new RecipeResponse(
                1L,
                "Jollof Rice",
                "Classic rice dish",
                "Cook rice with sauce",
                45,
                Difficulty.MEDIUM,
                4,
                "Nigerian",
                        "https://cdn.example.com/jollof.jpg",
                120,
                LocalDateTime.of(2026,4,1,10,0),
                10L,
                "Chef Obi",
                List.of("Rice", "Tomato", "Pepper")
                ), new RecipeResponse(
                        2L,
                        "Pancakes",
                        "Soft Pancakes",
                        "Mix and Fry",
                        20,
                        Difficulty.EASY,
                        2,
                        "English",
                        "https://cdn.example.com/pancakes.jpg",
                        55,
                        LocalDateTime.of(2026,4,1,11,0),
                        11L,
                        "Chef Ada",
                        List.of("Flour","Eggs", "Milk")
                ));

        when(recipeService.getAllRecipes()).thenReturn(recipes);

        // Act + Assert
        mockMvc.perform(get("/api/v1/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Jollof Rice"))
                .andExpect(jsonPath("$[0].difficulty").value("MEDIUM"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Pancakes"))
                .andExpect(jsonPath("$[1].difficulty").value("EASY"));
    }


    @Test
    void getRecipeById_shouldReturnRecipe() throws Exception {
        // Arrange: mock one recipe
        RecipeResponse recipe = new RecipeResponse(
                1L,
                "Jollof Rice",
                "Classic rice dish",
                "Cook rice with sauce",
                45,
                Difficulty.MEDIUM,
                4,
                "Nigerian",
                "https://cdn.example.com/jollof.jpg",
                120,
                LocalDateTime.of(2026,4,1,10,0),
                10L,
                "Chef Obi",
                List.of("Rice","Tomato", "Pepper")
        );

        when(recipeService.getRecipeById(1L)).thenReturn(recipe);

        // Act + Assert
        mockMvc.perform(get("/api/v1/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Jollof Rice"))
                .andExpect(jsonPath("$.description").value("Classic rice dish"))
                .andExpect(jsonPath("$.difficulty").value("MEDIUM"))
                .andExpect(jsonPath("$.chefName").value("Chef Obi"));
    }

    // POST TEST
    @Test
    void createRecipe_shouldReturnCreateRecipe() throws Exception {
        // Build an Authentication object the controller can read directly
        var auth = new UsernamePasswordAuthenticationToken(
                "chef@example.com",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_CHEF")
        );

        // Arrange: request DTO matching record
        RecipeCreateRequest request = new RecipeCreateRequest(
                "Jollof Rice",
                "Classic rice dish",
                "Cook rice with sauce",
                45,
                Difficulty.MEDIUM,
                4,
                "Nigerian",
                List.of("Rice","Tomato","Pepper")
        );

        // mocked service response
        RecipeResponse response = new RecipeResponse(
                1L,
                "Jollof Rice",
                "Classic rice dish",
                "Cook rice with sauce",
                45,
                Difficulty.MEDIUM,
                4,
                "Nigerian",
                "https://cdn.example.com/jollof.jpg",
                0,
                LocalDateTime.of(2026,4,2,12,0),
                10L,
                "Chef Obi",
                List.of("Rice", "Tomato", "Pepper")
        );

        when(recipeService.createRecipe(eq(request), eq("chef@example.com"))).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/v1/recipes")
                        .principal(auth)
                        .with(csrf()) // Needed for Post when Spring Security is active
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "title": "Jollof Rice",
                        "description": "Classic rice dish",
                        "instructions": "Cook rice with sauce",
                        "cookingTime": 45,
                        "difficulty": "MEDIUM",
                        "servings": 4,
                        "cuisineType": "Nigerian",
                        "Ingredients" : ["Rice", "Tomato", "Pepper"]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Jollof Rice"))
                .andExpect(jsonPath("$.difficulty").value("MEDIUM"))
                .andExpect(jsonPath("$.chefName").value("Chef Obi"));
    }

    //PUT TEST
    @Test
    void updateRecipe_shouldReturnUpdateRecipe() throws Exception {

        var auth = new UsernamePasswordAuthenticationToken(
                "chef@example.com",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_CHEF")
        );

        // update request matching record
        RecipeUpdateRequest request = new RecipeUpdateRequest(
                "Updated Jollof Rice",
                "Updated description",
                "Updated instructions",
                50,
                Difficulty.HARD,
                6,
                "Nigerian",
                List.of("Rice", "Tomato", "Pepper", "Onion")
        );

        RecipeResponse response = new RecipeResponse(
                1L,
                "Updated Jollof Rice",
                "Updated description",
                "Updated instructions",
                50,
                Difficulty.HARD,
                6,
                "Nigerian",
                "https://cdn.example.com/jollof-updated.jpg",
                150,
                LocalDateTime.of(2026,4,2,12,30),
                10L,
                "Chef Obi",
                List.of("Rice", "Tomato", "Pepper", "Onion")
        );

        when(recipeService.updateRecipe(eq(1L), eq(request),eq("chef@example.com"))).thenReturn(response);

        // Act + Assert
        mockMvc.perform(put("/api/v1/recipes/1")
                        .principal(auth)
                .with(csrf())  // Needed for Put when Spring Security is active
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "title": "Updated Jollof Rice",
                        "description": "Updated description",
                        "instructions": "Updated instructions",
                        "cookingTime": 50,
                        "difficulty": "HARD",
                        "servings": 6,
                        "cuisineType": "Nigerian",
                        "Ingredients": ["Rice","Tomato","Pepper","Onion"]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Jollof Rice"))
                .andExpect(jsonPath("$.difficulty").value("HARD"))
                .andExpect(jsonPath("$.servings").value(6));
    }


    //DELETE TEST
    @Test
    void deleteRecipe_shouldReturnNoContent() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                "chef@example.com",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_CHEF")
        );

        // mock delete call
        doNothing().when(recipeService).deleteRecipe(1L,"chef@example.com");

        // Act + Assert
        mockMvc.perform(delete("/api/v1/recipes/1")
                        .principal(auth)
                .with(csrf())) // Need for Delete when Spring Security is active
                .andExpect(status().isNoContent());

    }


}
