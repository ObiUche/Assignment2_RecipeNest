import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { useRecipes } from '../contexts/RecipeContext';
import { Recipe } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const RecipeDetail: React.FC = () => {
  // Read the recipe id from the URL
  const { id } = useParams();
  const recipeId = id ? Number(id) : null;

  // Try context first
  const { getRecipe } = useRecipes();
  const contextRecipe = recipeId !== null ? getRecipe(recipeId) : undefined;

  // Local state for fallback fetch
  const [recipe, setRecipe] = useState<Recipe | null>(contextRecipe ?? null);
  const [loading, setLoading] = useState(!contextRecipe && recipeId !== null);
  const [error, setError] = useState('');

  useEffect(() => {
    // Use context recipe if already available
    if (contextRecipe) {
      setRecipe(contextRecipe);
      setLoading(false);
      return;
    }

    // Stop early if id is invalid
    if (recipeId === null) {
      setLoading(false);
      setError('Invalid recipe id');
      return;
    }

    // Fetch directly from backend if context does not have it yet
    const fetchRecipe = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/recipes/${recipeId}`);

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(
            `Failed to load recipe: ${response.status} ${response.statusText} ${errorText}`
          );
        }

        const data: Recipe = await response.json();
        console.log('Recipe detail data:', data);
        setRecipe(data);
      } catch (err) {
        console.error(err);
        setError(err instanceof Error ? err.message : 'Recipe request failed');
      } finally {
        setLoading(false);
      }
    };

    fetchRecipe();
  }, [contextRecipe, recipeId]);

  if (loading) {
    return (
      <PageContainer>
        <Card>
          <div style={{ padding: '24px' }}>Loading recipe...</div>
        </Card>
      </PageContainer>
    );
  }

  if (error || !recipe) {
    return (
      <PageContainer>
        <Card>
          <div style={{ padding: '24px' }}>
            <h1 style={{ fontSize: '28px', marginBottom: '12px' }}>Recipe not found</h1>
            <p style={{ color: '#6B7280', marginBottom: '16px' }}>
              {error || 'The recipe you are looking for could not be found.'}
            </p>
            <Button to="/recipes" variant="secondary">
              Back to Recipes
            </Button>
          </div>
        </Card>
      </PageContainer>
    );
  }

  // Safe fallbacks so the page does not crash if backend misses optional fields
  const ingredients = Array.isArray(recipe.ingredients) ? recipe.ingredients : [];
  const photos = Array.isArray(recipe.photos) ? recipe.photos : [];

  return (
    <PageContainer>
      <div style={{ maxWidth: '900px', margin: '0 auto' }}>
        <div style={{ marginBottom: '20px' }}>
          <Link
            to="/recipes"
            style={{
              color: '#F97316',
              textDecoration: 'none',
              fontWeight: 600
            }}
          >
            ← Back to Recipes
          </Link>
        </div>

        <Card>
          <div style={{ padding: '24px' }}>
            {recipe.mainImage && (
              <img
                src={recipe.mainImage}
                alt={recipe.title}
                style={{
                  width: '100%',
                  maxHeight: '420px',
                  objectFit: 'cover',
                  borderRadius: '12px',
                  marginBottom: '24px'
                }}
              />
            )}

            <h1
              style={{
                fontSize: '36px',
                lineHeight: 1.1,
                marginBottom: '12px',
                color: '#1F2937'
              }}
            >
              {recipe.title}
            </h1>

            <p
              style={{
                color: '#6B7280',
                marginBottom: '20px',
                fontSize: '16px'
              }}
            >
              By {recipe.chefName ?? 'Unknown chef'}
            </p>

            <p
              style={{
                color: '#4B5563',
                fontSize: '18px',
                marginBottom: '24px'
              }}
            >
              {recipe.description}
            </p>

            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
                gap: '16px',
                marginBottom: '32px'
              }}
            >
              <div style={{ backgroundColor: '#FFF7ED', borderRadius: '12px', padding: '16px' }}>
                <p style={{ margin: '0 0 6px 0', color: '#9CA3AF', fontSize: '14px' }}>
                  Cooking Time
                </p>
                <strong style={{ color: '#C2410C' }}>{recipe.cookingTime} mins</strong>
              </div>

              <div style={{ backgroundColor: '#FFF7ED', borderRadius: '12px', padding: '16px' }}>
                <p style={{ margin: '0 0 6px 0', color: '#9CA3AF', fontSize: '14px' }}>
                  Difficulty
                </p>
                <strong style={{ color: '#C2410C' }}>{recipe.difficulty}</strong>
              </div>

              <div style={{ backgroundColor: '#FFF7ED', borderRadius: '12px', padding: '16px' }}>
                <p style={{ margin: '0 0 6px 0', color: '#9CA3AF', fontSize: '14px' }}>
                  Servings
                </p>
                <strong style={{ color: '#C2410C' }}>{recipe.servings}</strong>
              </div>

              <div style={{ backgroundColor: '#FFF7ED', borderRadius: '12px', padding: '16px' }}>
                <p style={{ margin: '0 0 6px 0', color: '#9CA3AF', fontSize: '14px' }}>
                  Cuisine
                </p>
                <strong style={{ color: '#C2410C' }}>{recipe.cuisineType}</strong>
              </div>
            </div>

            <section style={{ marginBottom: '32px' }}>
              <h2 style={{ fontSize: '26px', marginBottom: '16px', color: '#1F2937' }}>
                Ingredients
              </h2>

              {ingredients.length > 0 ? (
                <ul style={{ paddingLeft: '20px', color: '#374151', lineHeight: 1.8 }}>
                  {ingredients.map((ingredient, index) => (
                    <li key={`${ingredient}-${index}`}>{ingredient}</li>
                  ))}
                </ul>
              ) : (
                <p style={{ color: '#6B7280' }}>No ingredients available.</p>
              )}
            </section>

            <section style={{ marginBottom: '32px' }}>
              <h2 style={{ fontSize: '26px', marginBottom: '16px', color: '#1F2937' }}>
                Instructions
              </h2>

              <div
                style={{
                  whiteSpace: 'pre-line',
                  color: '#374151',
                  lineHeight: 1.8
                }}
              >
                {recipe.instructions || 'No instructions available.'}
              </div>
            </section>

            {photos.length > 0 && (
              <section>
                <h2 style={{ fontSize: '26px', marginBottom: '16px', color: '#1F2937' }}>
                  Gallery
                </h2>

                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                    gap: '16px'
                  }}
                >
                  {photos.map((photo) => (
                    <div key={photo.id}>
                      <img
                        src={photo.imageUrl}
                        alt={photo.caption || recipe.title}
                        style={{
                          width: '100%',
                          height: '180px',
                          objectFit: 'cover',
                          borderRadius: '12px',
                          border: photo.isPrimary ? '2px solid #F97316' : '1px solid #E5E7EB'
                        }}
                      />
                      {photo.caption && (
                        <p style={{ marginTop: '8px', fontSize: '14px', color: '#6B7280' }}>
                          {photo.caption}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              </section>
            )}
          </div>
        </Card>
      </div>
    </PageContainer>
  );
};