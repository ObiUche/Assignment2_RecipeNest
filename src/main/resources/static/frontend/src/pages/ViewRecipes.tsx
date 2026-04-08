import React from 'react';
import { PageContainer } from '../components/layout/PageContainer';
import { RecipeGrid } from '../components/recipe/RecipeGrid';
import { useRecipes } from '../contexts/RecipeContext';

export const ViewRecipes: React.FC = () => {
  const { recipes } = useRecipes();

  return (
    <PageContainer>
      <h1 style={{ fontSize: '32px', marginBottom: '16px' }}>All Recipes</h1>
      <RecipeGrid recipes={recipes} />
    </PageContainer>
  );
};