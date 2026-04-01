package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.admin.AdminDashboardResponse;
import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.entity.Recipe;
import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.repository.RecipeRepository;
import com.bedfordshire.recipenest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    // Mock repositories so we only test service log
    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    // Inject mocks into the service under test
    private AdminService adminService;

    private User recentUser;
    private Recipe recentRecipe;

    @BeforeEach
    void setUp(){
        // Build a recent user for dashboard testing
        recentUser = new User("Obinna", "Uche", "Obinna@example.com", "encodeded");
        recentUser.setId(1L);;
        recentUser.setRole(UserRole.CHEF);
        recentUser.setEmailVerified(true);
        recentUser.setActive(true);
        recentUser.setJoinDate(LocalDateTime.now());

        // Build a recent recipe for dashboard testing
        recentRecipe = new Recipe();
        recentRecipe.setId(10L);
        recentRecipe.setTitle("Jollof Rice");
        recentRecipe.setDescription("Classic rice dish");
        recentRecipe.setInstructions("Cook properly");
        recentRecipe.setCookingTime(60);
        recentRecipe.setDifficulty(Difficulty.MEDIUM);
        recentRecipe.setServings(4);
        recentRecipe.setCuisineType("Nigerian");
        recentRecipe.setViewCount(25);
        recentRecipe.setCreatedDate(LocalDateTime.now());
        recentRecipe.setChef(recentUser);
    }

    @Test
    void getDashboard_shouldReturnCorrectStatisticsAndRecentData(){
        // Mock user counts
        when(userRepository.count()).thenReturn(8L);
        when(userRepository.countByRole(UserRole.CHEF)).thenReturn(3L);
        when(userRepository.countByRole(UserRole.PUBLIC)).thenReturn(4L);
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);
        when(userRepository.countByEmailVerifiedTrue()).thenReturn(6L);
        when(userRepository.countByEmailVerifiedFalse()).thenReturn(2L);
        when(userRepository.countByAccountNonLockedFalse()).thenReturn(1L);
        // Mock recipe count
        when(recipeRepository.count()).thenReturn(6L);

        // Mock recent users page
        when(userRepository.findAll(PageRequest.of(0,5,org.springframework.data.domain.Sort.by(
                Sort.Direction.DESC, "joinDate"))))
                .thenReturn(new PageImpl<>(List.of(recentUser)));

        // Mock recent recipes page
        when(recipeRepository.findAll(PageRequest.of(0,5,org.springframework.data.domain.Sort.by(
                Sort.Direction.DESC,"createdDate"))))
                .thenReturn(new PageImpl<>(List.of(recentRecipe)));

        // Execute service method
        AdminDashboardResponse response = adminService.getDashboard();

        // Assert top-level statistics
        assertEquals(8L, response.totalUsers());
        assertEquals(3L, response.totalChefs());
        assertEquals(4L, response.totalPublicUsers());
        assertEquals(1L, response.totalAdmin());
        assertEquals(6L, response.totalVerifiedUsers());
        assertEquals(2L, response.totalUnverifiedUsers());
        assertEquals(1L, response.totalLockedUsers());
        assertEquals(6L, response.totalRecipes());

        // Assert recent users block
        assertEquals(1, response.recentUsers().size());
        assertEquals("Obinna", response.recentUsers().get(0).firstName());
        assertEquals(UserRole.CHEF, response.recentUsers().get(0).role());

        // Assert recent recipes block
        assertEquals(1, response.recentRecipes().size());
        assertEquals("Jollof Rice", response.recentRecipes().get(0).title());
        assertEquals(Difficulty.MEDIUM, response.recentRecipes().get(0).difficulty());





    }


}
