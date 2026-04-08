package com.bedfordshire.recipenest.config;

import com.bedfordshire.recipenest.entity.Difficulty;
import com.bedfordshire.recipenest.entity.Recipe;
import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.repository.RecipeRepository;
import com.bedfordshire.recipenest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    // User repository for inserting mock users
    private final UserRepository userRepository;

    // Recipe repository for inserting mock recipes
    private final RecipeRepository recipeRepository;

    // Password encoder so seeded passwords are stored securely
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(
            UserRepository userRepository,
            RecipeRepository recipeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only seed if the database is empty
        if (userRepository.count() > 0) {
            return;
        }

        // Create one admin user
        User admin = new User(
                "Admin",
                "User",
                "admin@recipe.com",
                passwordEncoder.encode("AdminPass123")
        );
        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);

        // Create one chef user
        User chef = new User(
                "Obinna",
                "Uche",
                "chef@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        chef.setRole(UserRole.CHEF);
        chef.setEmailVerified(true);
        chef.setCuisineSpeciality("Nigerian");
        chef.setLocation("London");
        chef.setBio("Chef focused on simple and flavorful homemade meals.");

        // Create one public user
        User publicUser = new User(
                "Sarah",
                "Reader",
                "public@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        publicUser.setRole(UserRole.PUBLIC);
        publicUser.setEmailVerified(false);

        // Save all seeded users first
        userRepository.saveAll(List.of(admin, chef, publicUser));

        // Create one recipe owned by the chef
        Recipe recipe = new Recipe();
        recipe.setTitle("Jollof Rice");
        recipe.setDescription("Classic smoky West African rice dish");
        recipe.setInstructions("Blend the base, fry it, add stock, then cook the rice.");
        recipe.setCookingTime(60);
        recipe.setDifficulty(Difficulty.MEDIUM);
        recipe.setServings(4);
        recipe.setCuisineType("Nigerian");
        recipe.setChef(chef);
        recipe.setIngredients(List.of("Rice", "Tomatoes", "Onion", "Pepper"));

        // Save the single seeded recipe
        recipeRepository.save(recipe);
    }
}