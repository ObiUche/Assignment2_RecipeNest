package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.admin.AdminDashboardResponse;
import com.bedfordshire.recipenest.dto.admin.RecentRecipeResponse;
import com.bedfordshire.recipenest.dto.admin.RecentUserResponse;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.repository.RecipeRepository;
import com.bedfordshire.recipenest.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {

    // Repository for user-related admin statistics
    private final UserRepository userRepository;

    // Repository for recipe-related admin statistics
    private final RecipeRepository recipeRepository;

    // Constructor injection
    public AdminService(UserRepository userRepository, RecipeRepository recipeRepository){
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public AdminDashboardResponse getDashboard(){
        // Count all users in the system
        long totalUsers = userRepository.count();

        // Count users by role
        long totalChefs = userRepository.countByRole(UserRole.CHEF);
        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
        long totalPublicUsers = userRepository.countByRole(UserRole.PUBLIC);


        // Count users by verification state
        long totalVerifiedUsers = userRepository.countByEmailVerifiedTrue();
        long totalUnverifiedUsers = userRepository.countByEmailVerifiedFalse();

        // Count locked Users
        long totalLockedUsers = userRepository.countByAccountNonLockedFalse();

        // Count all recipes
        long totalRecipes = recipeRepository.count();

        // Fetch the 5 user
        List<RecentUserResponse> recentUsers = userRepository.findAll(
                PageRequest.of(
                        0,5,Sort.by(Sort.Direction.DESC, "joinDate"))
                ).stream()
                .map(RecentUserResponse:: from)
                .toList();




        // Fetch the 5 most recent recipes
        List<RecentRecipeResponse> recentRecipes = recipeRepository.findAll(
                PageRequest.of(0,5, Sort.by(Sort.Direction.DESC, "createdDate"))
                    ).stream()
                .map(RecentRecipeResponse::from)
                .toList();

        // Build the admin dashboard response
        return new AdminDashboardResponse(
                totalUsers,
                totalChefs,
                totalPublicUsers,
                totalAdmins,
                totalVerifiedUsers,
                totalUnverifiedUsers,
                totalLockedUsers,
                totalRecipes,
                recentUsers,
                recentRecipes
        );


    }
}
