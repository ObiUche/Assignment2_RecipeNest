import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  AuthResponse,
  AuthState,
  AuthUser,
  UserProfile,
  UserRole,
  UserUpdateRequest
} from '../types';

// Context holds the auth state for the whole app.
// Undefined is used first so the custom hook can fail clearly if used outside the provider.
const AuthContext = createContext<AuthState | undefined>(undefined);

// Keep the backend base URL in one place for now.
// Later this can move into a dedicated API client file.
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Small helper key so auth data is stored consistently in localStorage.
const AUTH_STORAGE_KEY = 'recipenest_auth';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Logged-in user state shared across the app.
  // It stores auth tokens together with the current users profile details.
  const [user, setUser] = useState<AuthUser | null>(null);

  useEffect(() => {
    // Restore auth state after refresh so the user does not get logged out
    // every time the browser reloads during development.
    const storedAuth = localStorage.getItem(AUTH_STORAGE_KEY);

    if (!storedAuth) {
      return;
    }

    try {
      const parsedUser: AuthUser = JSON.parse(storedAuth);
      setUser(parsedUser);
    } catch (error) {
      console.error('Failed to parse stored auth data', error);
      localStorage.removeItem(AUTH_STORAGE_KEY);
    }
  }, []);

  const fetchMyProfile = async (accessToken: string): Promise<UserProfile | null> => {
    try {
      // Load the current logged-in users profile from the backend
      // so the frontend has real profile data instead of only email and role.
      const response = await fetch(`${API_BASE_URL}/users/me`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      });

      if (!response.ok) {
        return null;
      }

      const profile: UserProfile = await response.json();
      return profile;
    } catch (error) {
      console.error('Failed to fetch user profile', error);
      return null;
    }
  };

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      // Call the real backend login endpoint and send the credentials as JSON.
      // This matches the Spring Boot controller because login uses @RequestBody.
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email,
          password
        })
      });

      // Return false for invalid login instead of crashing the UI.
      if (!response.ok) {
        return false;
      }

      const data: AuthResponse = await response.json();

      // Load the full profile after login so the app can render first name,
      // bio, and other editable profile fields.
      const profile = await fetchMyProfile(data.accessToken);

      const loggedInUser: AuthUser = {
        id: profile?.id,
        firstName: profile?.firstName,
        lastName: profile?.lastName,
        email: data.email,
        profilePhoto: profile?.profilePhoto ?? null,
        location: profile?.location ?? null,
        bio: profile?.bio ?? null,
        cuisineSpeciality: profile?.cuisineSpeciality ?? null,
        role: data.role,
        joinDate: profile?.joinDate,
        accessToken: data.accessToken,
        refreshToken: data.refreshToken
      };

      setUser(loggedInUser);
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(loggedInUser));

      return true;
    } catch (error) {
      console.error('Login failed', error);
      return false;
    }
  };

  const logout = async (): Promise<void> => {
    // If a refresh token exists, try to revoke it in the backend first.
    // Even if that request fails, still clear frontend auth state so logout always works in the UI.
    if (user?.refreshToken) {
      try {
        await fetch(`${API_BASE_URL}/auth/logout`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            refreshToken: user.refreshToken
          })
        });
      } catch (error) {
        console.error('Logout request failed', error);
      }
    }

    setUser(null);
    localStorage.removeItem(AUTH_STORAGE_KEY);
  };

  const register = async (
    firstname: string,
    lastname: string,
    email: string,
    password: string,
    role: UserRole
  ): Promise<boolean> => {
    try {
      // Call the real backend register endpoint.
      // Registration returns success with no auth body, so success is based on response.ok.
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          firstname,
          lastname,
          email,
          password,
          role
        })
      });

      return response.ok;
    } catch (error) {
      console.error('Registration failed', error);
      return false;
    }
  };

  const updateMyProfile = async (request: UserUpdateRequest): Promise<boolean> => {
    // A logged-in user and access token are required before profile updates can be sent.
    if (!user?.accessToken) {
      return false;
    }

    try {
      // Send a partial text profile update to the backend using the current access token.
      const response = await fetch(`${API_BASE_URL}/users/me`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        return false;
      }

      const updatedProfile: UserProfile = await response.json();

      // Merge the updated backend profile with the existing auth tokens
      // so the user stays logged in after editing their profile.
      const updatedUser: AuthUser = {
        ...user,
        id: updatedProfile.id,
        firstName: updatedProfile.firstName,
        lastName: updatedProfile.lastName,
        email: updatedProfile.email,
        profilePhoto: updatedProfile.profilePhoto,
        location: updatedProfile.location,
        bio: updatedProfile.bio,
        cuisineSpeciality: updatedProfile.cuisineSpeciality,
        role: updatedProfile.role,
        joinDate: updatedProfile.joinDate
      };

      setUser(updatedUser);
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(updatedUser));

      return true;
    } catch (error) {
      console.error('Profile update failed', error);
      return false;
    }
  };

  const uploadProfilePhoto = async (file: File): Promise<boolean> => {
    // A logged-in user and access token are required before photo upload can be sent.
    if (!user?.accessToken) {
      return false;
    }

    try {
      // Build a multipart request because the backend photo endpoint
      // accepts a real uploaded file instead of JSON text data.
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch(`${API_BASE_URL}/users/me/photo`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user.accessToken}`
        },
        body: formData
      });

      if (!response.ok) {
        return false;
      }

      const updatedProfile: UserProfile = await response.json();

      // Merge the updated backend profile with the existing auth tokens
      // so the user stays logged in after uploading a new profile image.
      const updatedUser: AuthUser = {
        ...user,
        id: updatedProfile.id,
        firstName: updatedProfile.firstName,
        lastName: updatedProfile.lastName,
        email: updatedProfile.email,
        profilePhoto: updatedProfile.profilePhoto,
        location: updatedProfile.location,
        bio: updatedProfile.bio,
        cuisineSpeciality: updatedProfile.cuisineSpeciality,
        role: updatedProfile.role,
        joinDate: updatedProfile.joinDate
      };

      setUser(updatedUser);
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(updatedUser));

      return true;
    } catch (error) {
      console.error('Profile photo upload failed', error);
      return false;
    }
  };

  // Expose only the state and actions the rest of the app should use.
  const value: AuthState = {
    user,
    login,
    logout,
    register,
    updateMyProfile,
    uploadProfilePhoto
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Custom hook keeps auth access consistent and avoids repeating useContext logic.
export const useAuth = (): AuthState => {
  const context = useContext(AuthContext);

  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
};