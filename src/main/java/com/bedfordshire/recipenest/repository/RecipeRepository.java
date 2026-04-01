package com.bedfordshire.recipenest.repository;

import com.bedfordshire.recipenest.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {}
