import React, { useMemo } from 'react';
import { Card } from '../common/Card';
import { Recipe } from '../../types';
import { ChefCard } from './ChefCard';
import { useChefProfiles } from '../../hooks/useChefProfiles';

interface FeaturedChefsSectionProps {
  recipes: Recipe[];
}

export const FeaturedChefsSection: React.FC<FeaturedChefsSectionProps> = ({ recipes }) => {
  // Build a small list of visible chefs from the loaded recipes.
  // This keeps the section driven by real recipe data.
  const featuredChefs = useMemo(
    () =>
      Array.from(
        new Map(
          recipes
            .filter((recipe) => recipe.chefId !== null)
            .map((recipe) => [
              recipe.chefId,
              {
                id: recipe.chefId as number,
                name: recipe.chefName ?? 'Chef',
                count: recipes.filter((item) => item.chefId === recipe.chefId).length
              }
            ])
        ).values()
      ).slice(0, 4),
    [recipes]
  );

  // Extract chef ids so the hook can fetch public chef profiles.
  const chefIds = useMemo(
    () => featuredChefs.map((chef) => chef.id),
    [featuredChefs]
  );

  const { chefProfiles, isLoading } = useChefProfiles(chefIds);

  return (
    <section>
      <h2 style={{ fontSize: '32px', color: '#1F2937', marginBottom: '24px' }}>
        Featured Chefs
      </h2>

      {isLoading ? (
        <Card>
          <div style={{ padding: '24px', color: '#6B7280' }}>
            Loading chefs...
          </div>
        </Card>
      ) : featuredChefs.length > 0 ? (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
            gap: '20px'
          }}
        >
          {featuredChefs.map((chef) => (
            <ChefCard
              key={chef.id}
              chefId={chef.id}
              fallbackName={chef.name}
              recipeCount={chef.count}
              chefProfile={chefProfiles[chef.id]}
            />
          ))}
        </div>
      ) : (
        <Card>
          <div style={{ padding: '24px', color: '#6B7280' }}>
            No chefs to show yet.
          </div>
        </Card>
      )}
    </section>
  );
};