import React, { useState } from 'react';
import { PageContainer } from '../components/layout/PageContainer';
import { RecipeGrid } from '../components/recipe/RecipeGrid';
import { Button } from '../components/common/Button';
import { StatCard } from '../components/common/StatCard';
import { useAuth } from '../contexts/AuthContext';
import { useRecipes } from '../contexts/RecipeContext';
import { deleteRecipe } from '../api/recipeApi';

export const ChefDashboard: React.FC = () => {
  // Read the logged-in user from auth context.
  // The user object may now include first name and other profile fields.
  const { user } = useAuth();

  // Read the recipe list from recipe context.
  const { recipes, removeRecipe } = useRecipes();

  // Prefer first name for a friendlier greeting, then fall back to email.
  const displayName = user?.firstName || user?.email || 'Chef';

  // Track delete state for button disabling and page-level errors.
  const [deletingRecipeId, setDeletingRecipeId] = useState<number | null>(null);
  const [error, setError] = useState('');

  const handleDeleteRecipe = async (recipeId: number) => {
    if (!user?.accessToken) {
      setError('You must be logged in to delete recipes');
      return;
    }

    const confirmed = window.confirm('Are you sure you want to delete this recipe?');

    if (!confirmed) {
      return;
    }

    setDeletingRecipeId(recipeId);
    setError('');

    try {
      await deleteRecipe(recipeId, user.accessToken);
      removeRecipe(recipeId);
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Delete recipe failed');
    } finally {
      setDeletingRecipeId(null);
    }
  };

  return (
    <PageContainer>
      <h1 style={{ fontSize: '32px', marginBottom: '24px' }}>
        Welcome, {displayName}!
      </h1>

      {error && (
        <div
          style={{
            backgroundColor: '#FEE2E2',
            color: '#DC2626',
            padding: '12px',
            borderRadius: '8px',
            marginBottom: '16px'
          }}
        >
          {error}
        </div>
      )}

      <div style={{ marginBottom: '32px' }}>
        {/* Temporary recipe stat based on the currently loaded recipe list */}
        <StatCard label="Recipes" value={recipes.length} />
      </div>

      <div style={{ marginBottom: '32px' }}>
        {/* Main action for chefs on the dashboard */}
        <Button to="/recipe/new">+ Add New Recipe</Button>
      </div>

      <h2 style={{ fontSize: '24px', marginBottom: '16px' }}>Recipes</h2>

      {/* Show the currently loaded recipes for the chef dashboard */}
      <RecipeGrid
        recipes={recipes}
        showEditButton={true}
        showDeleteButton={true}
        deletingRecipeId={deletingRecipeId}
        onDeleteRecipe={handleDeleteRecipe}
      />
    </PageContainer>
  );
};