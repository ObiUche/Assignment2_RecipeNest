import React from 'react';
import { Button } from '../common/Button';
import { RecipePhoto } from '../../types';

interface RecipePhotoManagerProps {
  // Current recipe photos returned from the backend
  photos: RecipePhoto[];

  // Recipe title is only used for safer image alt text fallback
  recipeTitle: string;

  // Loading flag so buttons can be disabled during photo actions
  loading?: boolean;

  // Called when the user wants to make a photo the primary one
  onSetPrimary: (photoId: number) => void;

  // Called when the user wants to delete a photo
  onDelete: (photoId: number) => void;
}

export const RecipePhotoManager: React.FC<RecipePhotoManagerProps> = ({
  photos,
  recipeTitle,
  loading = false,
  onSetPrimary,
  onDelete
}) => {
  // If there are no photos yet, keep the UI simple and avoid rendering an empty grid
  if (!photos || photos.length === 0) {
    return null;
  }

  return (
    <div style={{ marginBottom: '24px' }}>
      {/* Section title for the recipe photo management area */}
      <h2 style={{ fontSize: '22px', marginBottom: '16px', color: '#1F2937' }}>
        Current Photos
      </h2>

      {/* Responsive photo grid for all recipe images */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: '16px'
        }}
      >
        {photos.map((photo) => (
          <div
            key={photo.id}
            style={{
              border: photo.isPrimary ? '2px solid #F97316' : '1px solid #E5E7EB',
              borderRadius: '12px',
              padding: '12px',
              backgroundColor: 'white'
            }}
          >
            {/* Recipe photo preview */}
            <img
              src={photo.imageUrl}
              alt={photo.caption || recipeTitle}
              style={{
                width: '100%',
                height: '180px',
                objectFit: 'cover',
                borderRadius: '8px',
                marginBottom: '12px'
              }}
            />

            {/* Small label showing whether this image is the main recipe image */}
            <p
              style={{
                margin: '0 0 12px 0',
                fontSize: '14px',
                color: '#6B7280',
                fontWeight: photo.isPrimary ? 700 : 400
              }}
            >
              {photo.isPrimary ? 'Primary Photo' : 'Recipe Photo'}
            </p>

            {/* Action buttons for photo management */}
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              {!photo.isPrimary && (
                <Button
                  type="button"
                  variant="secondary"
                  disabled={loading}
                  onClick={() => onSetPrimary(photo.id)}
                >
                  Set Primary
                </Button>
              )}

              <Button
                type="button"
                variant="secondary"
                disabled={loading}
                onClick={() => onDelete(photo.id)}
              >
                Delete
              </Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};