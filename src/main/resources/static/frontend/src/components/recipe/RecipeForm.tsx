import React from 'react';
import { Input } from '../common/Input';

interface RecipeFormData {
  title: string;
  description: string;
  instructions: string;
  cookingTime: string;
  difficulty: string;
  servings: string;
  cuisineType: string;
  ingredients: string;
}

interface RecipeFormProps {
  // Current form values controlled by the parent page
  formData: RecipeFormData;

  // Parent setter so the page keeps ownership of the form state
  setFormData: React.Dispatch<React.SetStateAction<RecipeFormData>>;

  // Selected image file is handled separately from the JSON DTO
  imageFile: File | null;

  // Parent setter for image file selection
  setImageFile: React.Dispatch<React.SetStateAction<File | null>>;
}

export const RecipeForm: React.FC<RecipeFormProps> = ({
  formData,
  setFormData,
  imageFile,
  setImageFile
}) => {
  return (
    <>
      {/* Basic recipe title */}
      <Input
        label="Title"
        value={formData.title}
        onChange={(e) => setFormData({ ...formData, title: e.target.value })}
        required
      />

      {/* Short recipe summary */}
      <Input
        label="Description"
        value={formData.description}
        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
        multiline
        rows={3}
        required
      />

      {/* Full cooking instructions */}
      <Input
        label="Instructions"
        value={formData.instructions}
        onChange={(e) => setFormData({ ...formData, instructions: e.target.value })}
        multiline
        rows={6}
        required
      />

      {/* Cooking time in minutes */}
      <Input
        label="Cooking Time (minutes)"
        type="number"
        value={formData.cookingTime}
        onChange={(e) => setFormData({ ...formData, cookingTime: e.target.value })}
        required
      />

      {/* Fixed enum select so frontend only sends valid backend values */}
      <div style={{ marginBottom: '16px' }}>
        <label
          htmlFor="difficulty"
          style={{
            display: 'block',
            marginBottom: '8px',
            fontWeight: 500
          }}
        >
          Difficulty
        </label>

        <select
          id="difficulty"
          value={formData.difficulty}
          onChange={(e) => setFormData({ ...formData, difficulty: e.target.value })}
          required
          style={{
            width: '100%',
            padding: '12px',
            borderRadius: '8px',
            border: '1px solid #D1D5DB',
            fontSize: '16px'
          }}
        >
          <option value="">Select difficulty</option>
          <option value="EASY">Easy</option>
          <option value="MEDIUM">Medium</option>
          <option value="HARD">Hard</option>
        </select>
      </div>

      {/* Number of servings */}
      <Input
        label="Servings"
        type="number"
        value={formData.servings}
        onChange={(e) => setFormData({ ...formData, servings: e.target.value })}
        required
      />

      {/* Cuisine type such as Nigerian, Indian, Italian */}
      <Input
        label="Cuisine Type"
        value={formData.cuisineType}
        onChange={(e) => setFormData({ ...formData, cuisineType: e.target.value })}
        required
      />

      {/* Ingredients are entered one per line and converted later by the parent */}
      <Input
        label="Ingredients (one per line)"
        value={formData.ingredients}
        onChange={(e) => setFormData({ ...formData, ingredients: e.target.value })}
        multiline
        rows={5}
        required
      />

      {/* Image upload is separate because backend handles it through multipart */}
      <div style={{ marginBottom: '16px' }}>
        <label
          htmlFor="recipe-image"
          style={{
            display: 'block',
            marginBottom: '8px',
            fontWeight: 500
          }}
        >
          Recipe Image
        </label>

        <input
          id="recipe-image"
          type="file"
          accept="image/*"
          onChange={(e) => setImageFile(e.target.files?.[0] ?? null)}
          style={{
            width: '100%',
            padding: '12px',
            borderRadius: '8px',
            border: '1px solid #D1D5DB',
            fontSize: '16px'
          }}
        />

        {/* Small helper text so the user knows what was selected */}
        {imageFile && (
          <p
            style={{
              marginTop: '8px',
              fontSize: '14px',
              color: '#6B7280'
            }}
          >
            Selected file: {imageFile.name}
          </p>
        )}
      </div>
    </>
  );
};