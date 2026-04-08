import React from 'react';
import { Link } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { RecipeGrid } from '../components/recipe/RecipeGrid';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import { FeaturedChefsSection } from '../components/chef/FeaturedChefsSection';
import { useRecipes } from '../contexts/RecipeContext';
import { useAuth } from '../contexts/AuthContext';

export const Homepage: React.FC = () => {
  // Read the shared recipe list so the homepage can show
  // featured content without needing a separate fetch here.
  const { recipes } = useRecipes();

  // Read auth state to slightly tailor the main call-to-action area.
  const { user } = useAuth();

  // Keep the homepage simple by featuring only the first few loaded recipes.
  const featuredRecipes = recipes.slice(0, 4);

  // Build a quick total of visible chefs from recipe data for the stat card.
  const totalVisibleChefs = new Set(
    recipes
      .filter((recipe) => recipe.chefId !== null)
      .map((recipe) => recipe.chefId)
  ).size;

  return (
    <PageContainer>
      <section
        style={{
          padding: '56px 32px',
          background:
            'linear-gradient(135deg, #FEF3C7 0%, #FED7AA 55%, #FDBA74 100%)',
          borderRadius: '20px',
          marginBottom: '48px',
          boxShadow: '0 12px 30px rgba(249, 115, 22, 0.12)'
        }}
      >
        <div
          style={{
            maxWidth: '720px'
          }}
        >
          <p
            style={{
              margin: '0 0 12px 0',
              color: '#C2410C',
              fontSize: '14px',
              fontWeight: 700,
              letterSpacing: '0.08em',
              textTransform: 'uppercase'
            }}
          >
            Discover. Create. Share.
          </p>

          <h1
            style={{
              fontSize: '48px',
              lineHeight: 1.1,
              color: '#1F2937',
              marginBottom: '16px'
            }}
          >
            Welcome to RecipeNest
          </h1>

          <p
            style={{
              fontSize: '20px',
              color: '#4B5563',
              marginBottom: '28px',
              maxWidth: '640px'
            }}
          >
            Explore recipes, manage your own creations, and build a cooking space
            that feels simple, modern, and easy to grow.
          </p>

          <div
            style={{
              display: 'flex',
              gap: '16px',
              flexWrap: 'wrap'
            }}
          >
            <Button to="/recipes">Browse Recipes</Button>

            {user?.role === 'CHEF' ? (
              <Button to="/recipe/new" variant="secondary">
                Create Recipe
              </Button>
            ) : user ? (
              <Button to="/" variant="secondary">
                Welcome Back
              </Button>
            ) : (
              <Button to="/register" variant="secondary">
                Join RecipeNest
              </Button>
            )}
          </div>
        </div>
      </section>

      <section
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
          gap: '20px',
          marginBottom: '48px'
        }}
      >
        <Card>
          <div style={{ padding: '20px' }}>
            <p
              style={{
                margin: '0 0 8px 0',
                color: '#9CA3AF',
                fontSize: '14px'
              }}
            >
              Total Recipes
            </p>
            <h3
              style={{
                margin: 0,
                fontSize: '32px',
                color: '#F97316'
              }}
            >
              {recipes.length}
            </h3>
          </div>
        </Card>

        <Card>
          <div style={{ padding: '20px' }}>
            <p
              style={{
                margin: '0 0 8px 0',
                color: '#9CA3AF',
                fontSize: '14px'
              }}
            >
              Featured Today
            </p>
            <h3
              style={{
                margin: 0,
                fontSize: '32px',
                color: '#F97316'
              }}
            >
              {featuredRecipes.length}
            </h3>
          </div>
        </Card>

        <Card>
          <div style={{ padding: '20px' }}>
            <p
              style={{
                margin: '0 0 8px 0',
                color: '#9CA3AF',
                fontSize: '14px'
              }}
            >
              Active Chefs
            </p>
            <h3
              style={{
                margin: 0,
                fontSize: '32px',
                color: '#F97316'
              }}
            >
              {totalVisibleChefs}
            </h3>
          </div>
        </Card>
      </section>

      <section style={{ marginBottom: '48px' }}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: '16px',
            flexWrap: 'wrap',
            marginBottom: '24px'
          }}
        >
          <h2 style={{ fontSize: '32px', color: '#1F2937', margin: 0 }}>
            Featured Recipes
          </h2>

          <Link
            to="/recipes"
            style={{
              color: '#F97316',
              textDecoration: 'none',
              fontWeight: 600
            }}
          >
            View all recipes
          </Link>
        </div>

        {featuredRecipes.length > 0 ? (
          <RecipeGrid recipes={featuredRecipes} />
        ) : (
          <Card>
            <div style={{ padding: '24px', color: '#6B7280' }}>
              No recipes available yet.
            </div>
          </Card>
        )}
      </section>

      <FeaturedChefsSection recipes={recipes} />
    </PageContainer>
  );
};