import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../common/Card';
import { PublicChefProfile } from '../../types';

interface ChefCardProps {
  chefId: number;
  fallbackName: string;
  recipeCount: number;
  chefProfile?: PublicChefProfile;
}

export const ChefCard: React.FC<ChefCardProps> = ({
  chefId,
  fallbackName,
  recipeCount,
  chefProfile
}) => {
  // Prefer the fetched backend full name, then fall back to recipe-derived name.
  const chefName = chefProfile?.fullName ?? fallbackName;

  // Prefer the fetched backend profile photo when available.
  const chefPhoto = chefProfile?.profilePhoto ?? null;

  // Build a simple initial fallback when no image exists.
  const chefInitial = chefName.charAt(0).toUpperCase();

  return (
    <Link
      to={`/chef/${chefId}`}
      style={{ textDecoration: 'none' }}
    >
      <Card>
        <div
          style={{
            padding: '24px',
            textAlign: 'center'
          }}
        >
          {chefPhoto ? (
            <img
              src={chefPhoto}
              alt={`${chefName} profile`}
              style={{
                width: '64px',
                height: '64px',
                borderRadius: '50%',
                objectFit: 'cover',
                border: '1px solid #E5E7EB',
                margin: '0 auto 16px',
                display: 'block'
              }}
            />
          ) : (
            <div
              style={{
                width: '64px',
                height: '64px',
                borderRadius: '50%',
                backgroundColor: '#FED7AA',
                color: '#C2410C',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 16px',
                fontSize: '24px',
                fontWeight: 700
              }}
            >
              {chefInitial}
            </div>
          )}

          <h3 style={{ color: '#1F2937', marginBottom: '8px' }}>
            {chefName}
          </h3>

          <p style={{ color: '#6B7280', margin: 0 }}>
            {recipeCount} recipe{recipeCount === 1 ? '' : 's'}
          </p>
        </div>
      </Card>
    </Link>
  );
};