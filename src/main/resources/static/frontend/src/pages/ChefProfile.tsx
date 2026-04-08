import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { RecipeGrid } from '../components/recipe/RecipeGrid';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import { useAuth } from '../contexts/AuthContext';
import { useRecipes } from '../contexts/RecipeContext';
import { PublicChefProfile } from '../types/index';

const API_BASE_URL = 'http://localhost:8080/api/v1';


export const ChefProfile: React.FC = () => {
  // Read the chef id from the route and convert it into a number
  // because recipe.chefId in the current frontend model is numeric.
  const { id } = useParams();
  const chefId = id ? Number(id) : null;

  // Read the logged-in user from auth context.
  // The auth user may include the real backend user id.
  const { user } = useAuth();

  // Read all recipes from recipe context so this page can derive
  // the chefs recipe list from real backend recipe data.
  const { recipes } = useRecipes();

  // Store the fetched public chef profile returned by the backend.
  const [chefProfile, setChefProfile] = useState<PublicChefProfile | null>(null);

  // Track loading state while the public chef profile is being fetched.
  const [isLoading, setIsLoading] = useState(true);

  // Filter recipes that belong to the chef in the URL.
  const chefRecipes =
    chefId !== null ? recipes.filter((recipe) => recipe.chefId === chefId) : [];

  // Check if the logged-in user is viewing their own profile.
  const isOwnProfile = user?.id === chefId;

  

  useEffect(() => {
    // Stop early if the route id is missing or invalid.
    if (chefId === null || Number.isNaN(chefId)) {
      setChefProfile(null);
      setIsLoading(false);
      return;
    }

    

    const fetchChefProfile = async () => {
      try {
        setIsLoading(true);

        // Load the chefs public profile details from the backend.
        const response = await fetch(`${API_BASE_URL}/users/${chefId}/public`);

        if (!response.ok) {
          setChefProfile(null);
          return;
        }

        const data: PublicChefProfile = await response.json();
        setChefProfile(data);
      } catch (error) {
        console.error('Failed to fetch chef profile', error);
        setChefProfile(null);
      } finally {
        setIsLoading(false);
      }
    };

    fetchChefProfile();
  }, [chefId]);

  // Show a loading state while the public chef profile is being fetched.
  if (isLoading) {
    return (
      <PageContainer>
        <div>Loading chef profile...</div>
      </PageContainer>
    );
  }

  // If the chef profile cannot be loaded, show not found.
  if (chefId === null || !chefProfile) {
    return (
      <PageContainer>
        <div>Chef not found</div>
      </PageContainer>
    );
  }

  // Build a simple fallback initial when no profile image exists.
  const chefInitial = chefProfile.firstName.charAt(0).toUpperCase();

  return (
    <PageContainer>
      <Card>
        <div style={{ padding: '24px' }}>
          <div
            style={{
              display: 'flex',
              alignItems: 'flex-start',
              gap: '24px',
              marginBottom: '24px'
            }}
          >
            {chefProfile.profilePhoto ? (
              <img
                src={chefProfile.profilePhoto}
                alt={`${chefProfile.fullName} profile`}
                style={{
                  width: '120px',
                  height: '120px',
                  borderRadius: '50%',
                  objectFit: 'cover',
                  border: '1px solid #E5E7EB',
                  flexShrink: 0
                }}
              />
            ) : (
              <div
                style={{
                  width: '120px',
                  height: '120px',
                  borderRadius: '50%',
                  backgroundColor: '#E5E7EB',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '36px',
                  fontWeight: 700,
                  color: '#374151',
                  flexShrink: 0
                }}
              >
                {chefInitial}
              </div>
            )}

            <div style={{ flex: 1 }}>
              <h1 style={{ fontSize: '36px', marginBottom: '8px' }}>
                {chefProfile.fullName}
              </h1>

              {chefProfile.location && (
                <p style={{ color: '#4B5563', marginBottom: '8px' }}>
                  {chefProfile.location}
                </p>
              )}

              {chefProfile.cuisineSpeciality && (
                <p style={{ color: '#4B5563', marginBottom: '8px' }}>
                  <strong>Cuisine Speciality:</strong> {chefProfile.cuisineSpeciality}
                </p>
              )}

              {chefProfile.bio && (
                <p style={{ color: '#4B5563', marginBottom: '16px' }}>
                  {chefProfile.bio}
                </p>
              )}

              <div style={{ display: 'flex', gap: '24px', marginBottom: '24px' }}>
                <div>
                  <strong>Recipes:</strong> {chefRecipes.length}
                </div>
              </div>

              {isOwnProfile && <Button to="/profile/edit">Edit Profile</Button>}
            </div>
          </div>
        </div>
      </Card>

      <h2 style={{ fontSize: '24px', margin: '32px 0 16px' }}>
        Recipes by {chefProfile.fullName}
      </h2>

      <RecipeGrid recipes={chefRecipes} showEditButton={isOwnProfile} />
    </PageContainer>
  );
};