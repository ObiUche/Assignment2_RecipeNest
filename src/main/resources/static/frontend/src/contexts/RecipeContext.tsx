import React, { createContext, useContext, useEffect, useState } from 'react';
import { Recipe, RecipeState } from '../types';

const RecipeContext = createContext<RecipeState | undefined>(undefined);

// Keep backend base URL in one place for now.
// Later this can move into a shared API client file.
const API_BASE_URL = 'http://localhost:8080/api/v1';

export const RecipeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Shared recipe state used by recipe pages across the app.
  const [recipes, setRecipes] = useState<Recipe[]>([]);

  useEffect(() => {
    // Load public recipes from the backend when the provider first mounts.
    // This replaces the old mock-data bootstrapping used by the demo.
    const fetchRecipes = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/recipes`);

        if (!response.ok) {
          throw new Error('Failed to fetch recipes');
        }

        const data: Recipe[] = await response.json();
        setRecipes(data);
      } catch (error) {
        console.error('Failed to load recipes', error);
      }
    };

    fetchRecipes();
  }, []);

  const getRecipe = (id: number): Recipe | undefined => {
    // Find one recipe already loaded into context state.
    return recipes.find((recipe) => recipe.id === id);
  };

  const getUserRecipes = (chefId: number): Recipe[] => {
    // Filter recipes owned by one chef for profile/dashboard style views.
    return recipes.filter((recipe) => recipe.chefId === chefId);
  };

  const value: RecipeState = {
    recipes,
    setRecipes,
    getRecipe,
    getUserRecipes
  };

  return <RecipeContext.Provider value={value}>{children}</RecipeContext.Provider>;
};

export const useRecipes = (): RecipeState => {
  const context = useContext(RecipeContext);

  if (context === undefined) {
    throw new Error('useRecipes must be used within a RecipeProvider');
  }

  return context;
};