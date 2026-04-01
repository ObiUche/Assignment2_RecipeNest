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
public class DevDataSeeder implements CommandLineRunner{

    // User repository for inserting mock users
    private final UserRepository userRepository;

    // Recipe repository for inserting mock recipes
    private final RecipeRepository recipeRepository;

    // Password encoded so seeded passwords are stored securely
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
    public void run(String... args){
        // Only seed if the database is empty
        if(userRepository.count() > 0){
            return;
        }

        // Create admin
        User admin = new User(
                "Admin",
                "User",
                "admin@recipe.com",
                passwordEncoder.encode("AdminPass123")
        );
        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);


        // Create chef
        User chef1 = new User(
                "Obinna",
                "Uche",
                "obinnauche01@outlook.com",
                passwordEncoder.encode("Password123"));
        chef1.setRole(UserRole.CHEF);
        chef1.setEmailVerified(true);
        chef1.setCuisineSpeciality("Nigerian");
        User chef2 = new User(
                "Amara",
                "Cook",
                "chef2@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        chef2.setRole(UserRole.CHEF);
        chef2.setEmailVerified(true);
        chef2.setCuisineSpeciality("Italian");

        User chef3 = new User(
                "Tobi",
                "Kitchen",
                "chef3@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        chef3.setRole(UserRole.CHEF);
        chef3.setEmailVerified(true);
        chef3.setCuisineSpeciality("Indian");

        // --------------------------
        // Create public users
        // --------------------------
        User public1 = new User(
                "Sarah",
                "Reader",
                "public1@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        public1.setRole(UserRole.PUBLIC);
        public1.setEmailVerified(true);

        User public2 = new User(
                "James",
                "Viewer",
                "public2@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        public2.setRole(UserRole.PUBLIC);
        public2.setEmailVerified(false);

        User public3 = new User(
                "Maya",
                "Guest",
                "public3@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        public3.setRole(UserRole.PUBLIC);
        public3.setEmailVerified(true);

        // Create one locked account for dashboard stats
        User lockedUser = new User(
                "Locked",
                "User",
                "locked@recipenest.com",
                passwordEncoder.encode("Password123")
        );
        lockedUser.setRole(UserRole.PUBLIC);
        lockedUser.setEmailVerified(true);
        lockedUser.setAccountNonLocked(false);

        // Save all users first
        userRepository.saveAll(List.of(
                admin,
                chef1,
                chef2,
                chef3,
                public1,
                public2,
                public3,
                lockedUser
        ));

        // --------------------------
        // Create recipes
        // --------------------------
        Recipe recipe1 = new Recipe();
        recipe1.setTitle("Jollof Rice");
        recipe1.setDescription("Classic smoky West African rice dish");
        recipe1.setInstructions("Blend the base, fry it, add stock, then cook the rice.");
        recipe1.setCookingTime(60);
        recipe1.setDifficulty(Difficulty.MEDIUM);
        recipe1.setServings(4);
        recipe1.setCuisineType("Nigerian");
        recipe1.setChef(chef1);
        recipe1.setIngredients(List.of("Rice", "Tomatoes", "Onion", "Pepper"));

        Recipe recipe2 = new Recipe();
        recipe2.setTitle("Egusi Soup");
        recipe2.setDescription("Rich melon seed soup");
        recipe2.setInstructions("Cook meat, prepare egusi paste, simmer with stock and greens.");
        recipe2.setCookingTime(75);
        recipe2.setDifficulty(Difficulty.HARD);
        recipe2.setServings(5);
        recipe2.setCuisineType("Nigerian");
        recipe2.setChef(chef1);
        recipe2.setIngredients(List.of("Egusi", "Spinach", "Palm oil", "Beef"));

        Recipe recipe3 = new Recipe();
        recipe3.setTitle("Spaghetti Carbonara");
        recipe3.setDescription("Creamy Italian pasta");
        recipe3.setInstructions("Cook pasta, prepare sauce, combine with cheese and eggs.");
        recipe3.setCookingTime(25);
        recipe3.setDifficulty(Difficulty.MEDIUM);
        recipe3.setServings(2);
        recipe3.setCuisineType("Italian");
        recipe3.setChef(chef2);
        recipe3.setIngredients(List.of("Spaghetti", "Eggs", "Parmesan", "Pancetta"));

        Recipe recipe4 = new Recipe();
        recipe4.setTitle("Margherita Pizza");
        recipe4.setDescription("Simple tomato and mozzarella pizza");
        recipe4.setInstructions("Prepare dough, add sauce and cheese, then bake.");
        recipe4.setCookingTime(40);
        recipe4.setDifficulty(Difficulty.MEDIUM);
        recipe4.setServings(3);
        recipe4.setCuisineType("Italian");
        recipe4.setChef(chef2);
        recipe4.setIngredients(List.of("Pizza dough", "Tomato sauce", "Mozzarella", "Basil"));

        Recipe recipe5 = new Recipe();
        recipe5.setTitle("Chicken Curry");
        recipe5.setDescription("Spiced curry with tender chicken");
        recipe5.setInstructions("Cook onions and spices, add chicken, simmer with sauce.");
        recipe5.setCookingTime(50);
        recipe5.setDifficulty(Difficulty.MEDIUM);
        recipe5.setServings(4);
        recipe5.setCuisineType("Indian");
        recipe5.setChef(chef3);
        recipe5.setIngredients(List.of("Chicken", "Onion", "Garlic", "Curry spices"));

        Recipe recipe6 = new Recipe();
        recipe6.setTitle("Butter Paneer");
        recipe6.setDescription("Creamy tomato-based paneer curry");
        recipe6.setInstructions("Make tomato base, add cream and paneer, simmer gently.");
        recipe6.setCookingTime(35);
        recipe6.setDifficulty(Difficulty.EASY);
        recipe6.setServings(3);
        recipe6.setCuisineType("Indian");
        recipe6.setChef(chef3);
        recipe6.setIngredients(List.of("Paneer", "Tomato", "Cream", "Butter"));

        recipeRepository.saveAll(List.of(
                recipe1,
                recipe2,
                recipe3,
                recipe4,
                recipe5,
                recipe6
        ));
    }

}

