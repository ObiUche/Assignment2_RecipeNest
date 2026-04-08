import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { useAuth } from '../contexts/AuthContext';
import { useRecipes } from '../contexts/RecipeContext';
import { Recipe } from '../types';
import { RecipeForm } from '../components/recipe/RecipeForm';
import { RecipePhotoManager } from '../components/recipe/RecipePhotoManager';
import {
  createRecipe,
  updateRecipe,
  uploadRecipePhoto,
  replaceRecipePhoto,
  setPrimaryRecipePhoto,
  deleteRecipePhoto
} from '../api/recipeApi';

export const EditRecipe: React.FC = () => {
  // Read the route param to decide whether this page is creating or editing
  const { id } = useParams();

  // Navigation is used after save and for cancel
  const navigate = useNavigate();

  // Read auth state so protected requests can include the access token
  const { user } = useAuth();

  // Read recipe context so edit mode can prefill and update local frontend state
  const { getRecipe, setRecipes, recipes } = useRecipes();

  // Convert route param into a number because backend recipe IDs are numeric
  const recipeId = id ? Number(id) : null;

  // Existing recipe is used when editing
  const existingRecipe = recipeId ? getRecipe(recipeId) : undefined;

  // Main form state aligned with the backend DTO shape
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    instructions: '',
    cookingTime: '',
    difficulty: '',
    servings: '',
    cuisineType: '',
    ingredients: ''
  });

  // Separate image file state because image upload uses multipart in a different endpoint
  const [imageFile, setImageFile] = useState<File | null>(null);

  // Keep local recipe state so photo actions can update the screen immediately
  const [currentRecipe, setCurrentRecipe] = useState<Recipe | null>(existingRecipe ?? null);

  // Page UI state
  const [loading, setLoading] = useState(false);
  const [photoActionLoading, setPhotoActionLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    // Prefill the form when editing an existing recipe
    if (existingRecipe) {
      setCurrentRecipe(existingRecipe);
      setFormData({
        title: existingRecipe.title,
        description: existingRecipe.description,
        instructions: existingRecipe.instructions,
        cookingTime: String(existingRecipe.cookingTime),
        difficulty: existingRecipe.difficulty,
        servings: String(existingRecipe.servings),
        cuisineType: existingRecipe.cuisineType,
        ingredients: existingRecipe.ingredients.join('\n')
      });
    }
  }, [existingRecipe]);

  const syncRecipeIntoContext = (updatedRecipe: Recipe) => {
    // Keep the shared recipe context aligned with the latest backend state
    if (recipeId) {
      setRecipes(
        recipes.map((recipe) => (recipe.id === updatedRecipe.id ? updatedRecipe : recipe))
      );
    } else {
      setRecipes([updatedRecipe, ...recipes]);
    }

    setCurrentRecipe(updatedRecipe);
  };

  const buildRecipePayload = () => {
    // Match the backend DTO exactly
    return {
      title: formData.title,
      description: formData.description,
      instructions: formData.instructions,
      cookingTime: Number(formData.cookingTime),
      difficulty: formData.difficulty,
      servings: Number(formData.servings),
      cuisineType: formData.cuisineType,
      Ingredients: formData.ingredients
        .split('\n')
        .map((item) => item.trim())
        .filter((item) => item.length > 0)
    };
  };

  const handleSetPrimaryPhoto = async (photoId: number) => {
    if (!user?.accessToken || !currentRecipe) {
      setError('You must be logged in to manage recipe photos');
      return;
    }

    setPhotoActionLoading(true);
    setError('');

    try {
      const updatedRecipe = await setPrimaryRecipePhoto(
        currentRecipe.id,
        photoId,
        user.accessToken
      );

      syncRecipeIntoContext(updatedRecipe);
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Set primary photo failed');
    } finally {
      setPhotoActionLoading(false);
    }
  };

  const handleDeletePhoto = async (photoId: number) => {
    if (!user?.accessToken || !currentRecipe) {
      setError('You must be logged in to manage recipe photos');
      return;
    }

    setPhotoActionLoading(true);
    setError('');

    try {
      const updatedRecipe = await deleteRecipePhoto(
        currentRecipe.id,
        photoId,
        user.accessToken
      );

      syncRecipeIntoContext(updatedRecipe);
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Delete photo failed');
    } finally {
      setPhotoActionLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user?.accessToken) {
      setError('You must be logged in to manage recipes');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // Save the recipe first through the JSON endpoint
      const savedBaseRecipe = recipeId
        ? await updateRecipe(recipeId, buildRecipePayload(), user.accessToken)
        : await createRecipe(buildRecipePayload(), user.accessToken);

      let savedRecipe = savedBaseRecipe;

      // If a file was selected, upload or replace through the multipart photo endpoints
      if (imageFile) {
        const primaryPhoto = savedBaseRecipe.photos?.find((photo) => photo.isPrimary);

        savedRecipe = primaryPhoto
          ? await replaceRecipePhoto(
              savedBaseRecipe.id,
              primaryPhoto.id,
              imageFile,
              user.accessToken
            )
          : await uploadRecipePhoto(savedBaseRecipe.id, imageFile, user.accessToken);
      }

      syncRecipeIntoContext(savedRecipe);
      navigate('/chef/dashboard');
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Recipe request failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '900px', margin: '0 auto' }}>
        <h1 style={{ fontSize: '32px', marginBottom: '24px' }}>
          {existingRecipe ? 'Edit Recipe' : 'New Recipe'}
        </h1>

        <Card>
          <form onSubmit={handleSubmit} style={{ padding: '24px' }}>
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

            <RecipeForm
              formData={formData}
              setFormData={setFormData}
              imageFile={imageFile}
              setImageFile={setImageFile}
            />

            <RecipePhotoManager
              photos={currentRecipe?.photos ?? []}
              recipeTitle={currentRecipe?.title ?? formData.title}
              loading={photoActionLoading}
              onSetPrimary={handleSetPrimaryPhoto}
              onDelete={handleDeletePhoto}
            />

            <div style={{ display: 'flex', gap: '16px', marginTop: '24px' }}>
              <Button type="submit" disabled={loading}>
                {loading
                  ? existingRecipe
                    ? 'Updating...'
                    : 'Creating...'
                  : existingRecipe
                    ? 'Update Recipe'
                    : 'Create Recipe'}
              </Button>

              <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                Cancel
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </PageContainer>
  );
};