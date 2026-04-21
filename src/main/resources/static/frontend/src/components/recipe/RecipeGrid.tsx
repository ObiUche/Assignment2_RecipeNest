import React from 'react';
import { Recipe } from '../../types';
import { RecipeCard } from './RecipeCard';

interface RecipeGridProps {
  recipes: Recipe[];
  showEditButton?: boolean;
  showDeleteButton?: boolean;
  deletingRecipeId?: number | null;
  onDeleteRecipe?: (recipeId: number) => void;
}

export const RecipeGrid: React.FC<RecipeGridProps> = ({
  recipes,
  showEditButton = false,
  showDeleteButton = false,
  deletingRecipeId = null,
  onDeleteRecipe
}) => {
  const styles = {
    grid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
      gap: '24px',
      marginTop: '24px'
    }
  };

  if (recipes.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '48px', color: '#6B7280' }}>
        No recipes found
      </div>
    );
  }

  return (
    <div style={styles.grid}>
      {recipes.map((recipe) => (
        <RecipeCard
          key={recipe.id}
          recipe={recipe}
          showEditButton={showEditButton}
          showDeleteButton={showDeleteButton}
          isDeleting={deletingRecipeId === recipe.id}
          onDelete={onDeleteRecipe}
        />
      ))}
    </div>
  );
};