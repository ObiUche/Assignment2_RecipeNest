import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../common/Card';
import { Recipe } from '../../types';
import { Button } from '../common/Button';

interface RecipeCardProps {
  recipe: Recipe;
  showEditButton?: boolean;
  showDeleteButton?: boolean;
  isDeleting?: boolean;
  onDelete?: (recipeId: number) => void;
}

export const RecipeCard: React.FC<RecipeCardProps> = ({
  recipe,
  showEditButton = false,
  showDeleteButton = false,
  isDeleting = false,
  onDelete
}) => {
  return (
    <Card>
      {/* Wrap the main card content in a recipe-detail link so users can open the full recipe */}
      <Link
        to={`/recipes/${recipe.id}`}
        style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}
      >
        {recipe.mainImage && (
          <img
            src={recipe.mainImage}
            alt={recipe.title}
            style={{
              width: '100%',
              height: '200px',
              objectFit: 'cover',
              borderRadius: '8px',
              marginBottom: '12px'
            }}
          />
        )}

        <h3 style={{ margin: '0 0 8px 0', color: '#1F2937' }}>{recipe.title}</h3>

        <p style={{ margin: '0 0 8px 0', color: '#6B7280', fontSize: '14px' }}>
          By {recipe.chefName ?? 'Unknown chef'}
        </p>

        <p style={{ margin: '0 0 8px 0', color: '#6B7280', fontSize: '14px' }}>
          {recipe.cookingTime} mins • {recipe.difficulty}
        </p>

        <p style={{ margin: '0 0 16px 0', color: '#4B5563' }}>
          {recipe.description.length > 100
            ? `${recipe.description.substring(0, 100)}...`
            : recipe.description}
        </p>
      </Link>

      {showEditButton ? (
        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <Button to={`/recipe/edit/${recipe.id}`} variant="secondary">
            Edit Recipe
          </Button>

          {showDeleteButton && onDelete && (
            <Button
              type="button"
              variant="secondary"
              onClick={() => onDelete(recipe.id)}
              disabled={isDeleting}
            >
              {isDeleting ? 'Deleting...' : 'Delete Recipe'}
            </Button>
          )}
        </div>
      ) : (
        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <Button to={`/recipes/${recipe.id}`} variant="secondary">
            View Recipe
          </Button>

          <Link to={`/chef/${recipe.chefId}`} style={{ textDecoration: 'none' }}>
            <Button variant="secondary">View Chef</Button>
          </Link>
        </div>
      )}
    </Card>
  );
};