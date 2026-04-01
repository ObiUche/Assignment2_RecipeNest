package com.bedfordshire.recipenest.controller;

import com.bedfordshire.recipenest.dto.admin.AdminDashboardResponse;
import com.bedfordshire.recipenest.dto.admin.RecentRecipeResponse;
import com.bedfordshire.recipenest.dto.admin.RecentUserResponse;
import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Loads only the MVC layer for AdminController
@WebMvcTest(AdminController.class)

// Disables the security filter chain for this controller-slice test
// so we test request -> controller -> response

@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    // MockMvc lets us test the controller without starting a server
    @Autowired
    private MockMvc mockMvc;

    // Mock the service dependency used by the controller
    @MockitoBean
    private AdminService adminService;

    @Test
    void getDashboard_shouldReturnDashboardResponse() throws Exception {
        // Build fake recent-user data for response
        RecentUserResponse recentUser = new RecentUserResponse(
                1L,
                "Obinna",
                "Uche",
                "Obinnauche01@outlook.com",
                UserRole.CHEF,
                true,
                true,
                LocalDateTime.of(2026,4,1,12,0)
        );

        // Build fake recent-recipe data for response
        RecentRecipeResponse recentRecipe = new RecentRecipeResponse(
                10L,
                "Jollof Rice",
                Difficulty.MEDIUM,
                "Nigerian",
                4,
                25,
                LocalDateTime.of(2026,4,1,12,0),
                1L,
                "Obinna Uche"
        );

        // Build the full dashboard DTO returned by the mocked service
        AdminDashboardResponse dashboardResponse = new AdminDashboardResponse(
                8L,
                3L,
                4L,
                1L,
                6L,
                2L,
                1L
                ,6L,
                List.of(recentUser),
                List.of(recentRecipe));

        // Tell the mock service what to return
        when(adminService.getDashboard()).thenReturn(dashboardResponse);

        // Call the endpoint and assert the JSON response
        mockMvc.perform(get("/api/v1/admin/dashboard")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalUsers").value(8))
                .andExpect(jsonPath("$.totalChefs").value(3))
                .andExpect(jsonPath("$.totalPublicUsers").value(4))
                .andExpect(jsonPath("$.totalAdmin").value(1))
                .andExpect(jsonPath("$.totalVerifiedUsers").value(6))
                .andExpect(jsonPath("$.totalUnverifiedUsers").value(2))
                .andExpect(jsonPath("$.totalLockedUsers").value(1))
                .andExpect(jsonPath("$.totalRecipes").value(6))
                .andExpect(jsonPath("$.recentUsers[0].email").value("Obinnauche01@outlook.com"))
                .andExpect(jsonPath("$.recentUsers[0].role").value("CHEF"))
                .andExpect(jsonPath("$.recentRecipes[0].title").value("Jollof Rice"))
                .andExpect(jsonPath("$.recentRecipes[0].difficulty").value("MEDIUM"))
                .andExpect(jsonPath("$.recentRecipes[0].chefName").value("Obinna Uche"));



    }
}
