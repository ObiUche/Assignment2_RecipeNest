import React from 'react';
import { PageContainer } from '../components/layout/PageContainer';
import { RecipeGrid } from '../components/recipe/RecipeGrid';
import { Button } from '../components/common/Button';
import { StatCard } from '../components/common/StatCard';
import { useAuth } from '../contexts/AuthContext';
import { useRecipes } from '../contexts/RecipeContext';

export const ChefDashboard: React.FC = () => {
  // Read the logged-in user from auth context.
  // The user object may now include first name and other profile fields.
  const { user } = useAuth();

  // Read the recipe list from recipe context.
  const { recipes } = useRecipes();

  // Prefer first name for a friendlier greeting, then fall back to email.
  const displayName = user?.firstName || user?.email || 'Chef';

  return (
    <PageContainer>
      <h1 style={{ fontSize: '32px', marginBottom: '24px' }}>
        Welcome, {displayName}!
      </h1>

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
      <RecipeGrid recipes={recipes} showEditButton={true} />
    </PageContainer>
  );
};