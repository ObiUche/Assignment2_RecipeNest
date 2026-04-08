import { useEffect, useState } from 'react';
import { PublicChefProfile } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const useChefProfiles = (chefIds: number[]) => {
  // Store fetched chef profiles by chef id for quick lookup.
  const [chefProfiles, setChefProfiles] = useState<Record<number, PublicChefProfile>>({});

  // Track loading state while chef profiles are being fetched.
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // Stop early when there are no chef ids to fetch.
    if (chefIds.length === 0) {
      setChefProfiles({});
      setIsLoading(false);
      return;
    }

    const fetchChefProfiles = async () => {
      try {
        setIsLoading(true);

        const responses = await Promise.all(
          chefIds.map(async (chefId) => {
            const response = await fetch(`${API_BASE_URL}/users/${chefId}/public`);

            if (!response.ok) {
              return null;
            }

            const data: PublicChefProfile = await response.json();
            return data;
          })
        );

        const nextProfiles: Record<number, PublicChefProfile> = {};

        responses.forEach((profile) => {
          if (profile) {
            nextProfiles[profile.id] = profile;
          }
        });

        setChefProfiles(nextProfiles);
      } catch (error) {
        console.error('Failed to fetch chef profiles', error);
        setChefProfiles({});
      } finally {
        setIsLoading(false);
      }
    };

    fetchChefProfiles();
  }, [chefIds]);

  return {
    chefProfiles,
    isLoading
  };
};