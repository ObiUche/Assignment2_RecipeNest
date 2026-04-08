import { Recipe } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/v1';

// Shared helper for JSON recipe create/update requests
// so the page code stays smaller and easier to read.
const sendJsonRecipeRequest = async (
  url: string,
  method: 'POST' | 'PUT',
  payload: unknown,
  accessToken: string
): Promise<Recipe> => {
  const response = await fetch(url, {
    method,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${accessToken}`
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `${method} failed: ${response.status} ${response.statusText} ${errorText}`
    );
  }

  return response.json();
};

// Shared helper for multipart photo endpoints
// so upload and replace reuse the same logic.
const sendPhotoRequest = async (
  url: string,
  method: 'POST' | 'PUT',
  file: File,
  accessToken: string
): Promise<Recipe> => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(url, {
    method,
    headers: {
      Authorization: `Bearer ${accessToken}`
    },
    body: formData
  });

  const responseText = await response.text();

  if (!response.ok) {
    throw new Error(
      `Photo request failed: ${response.status} ${response.statusText} ${responseText}`
    );
  }

  return responseText ? JSON.parse(responseText) : ({} as Recipe);
};

export const createRecipe = async (
  payload: unknown,
  accessToken: string
): Promise<Recipe> => {
  return sendJsonRecipeRequest(
    `${API_BASE_URL}/recipes`,
    'POST',
    payload,
    accessToken
  );
};

export const updateRecipe = async (
  recipeId: number,
  payload: unknown,
  accessToken: string
): Promise<Recipe> => {
  return sendJsonRecipeRequest(
    `${API_BASE_URL}/recipes/${recipeId}`,
    'PUT',
    payload,
    accessToken
  );
};

export const uploadRecipePhoto = async (
  recipeId: number,
  file: File,
  accessToken: string
): Promise<Recipe> => {
  return sendPhotoRequest(
    `${API_BASE_URL}/recipes/${recipeId}/photos`,
    'POST',
    file,
    accessToken
  );
};

export const replaceRecipePhoto = async (
  recipeId: number,
  photoId: number,
  file: File,
  accessToken: string
): Promise<Recipe> => {
  return sendPhotoRequest(
    `${API_BASE_URL}/recipes/${recipeId}/photos/${photoId}`,
    'PUT',
    file,
    accessToken
  );
};

export const setPrimaryRecipePhoto = async (
  recipeId: number,
  photoId: number,
  accessToken: string
): Promise<Recipe> => {
  const response = await fetch(
    `${API_BASE_URL}/recipes/${recipeId}/photos/${photoId}/primary`,
    {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Set primary failed: ${response.status} ${response.statusText} ${errorText}`
    );
  }

  return response.json();
};

export const deleteRecipePhoto = async (
  recipeId: number,
  photoId: number,
  accessToken: string
): Promise<Recipe> => {
  const response = await fetch(
    `${API_BASE_URL}/recipes/${recipeId}/photos/${photoId}`,
    {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
  );

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Delete photo failed: ${response.status} ${response.statusText} ${errorText}`
    );
  }

  return response.json();
};