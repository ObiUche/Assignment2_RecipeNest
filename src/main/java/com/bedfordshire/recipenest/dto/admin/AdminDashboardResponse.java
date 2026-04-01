package com.bedfordshire.recipenest.dto.admin;

import java.util.List;

public record AdminDashboardResponse(
        // Total number of users in the system
        long totalUsers,

        // Number of users with CHEF role
        long totalChefs,

        // Number of users with PUBLIC role
        long totalPublicUsers,

        // Number of users with ADMIN role
        long totalAdmin,

        // Number of users who have verified their email
        long totalVerifiedUsers,

        // Number of users who have not verified their email
        long totalUnverifiedUsers,

        // Number of users whose account is currently locked
        long totalLockedUsers,

        // Total number of recipes in the system
        long totalRecipes,

        // Small recent-users summary for admin cards/tables
        List<RecentUserResponse> recentUsers,

        // Small recent-recipes summary for admin dashboard
        List<RecentRecipeResponse> recentRecipes

) {}
