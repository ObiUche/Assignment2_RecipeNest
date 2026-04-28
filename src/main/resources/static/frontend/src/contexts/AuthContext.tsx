import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  AuthResponse,
  AuthState,
  AuthUser,
  RegisterResult,
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

// Reads backend error responses and turns them into a user-friendly message.
// This supports plain text errors and common JSON error shapes from Spring Boot.
const extractErrorMessage = async (response: Response): Promise<string> => {
  const responseText = await response.text();

  if (!responseText) {
    return `${response.status} ${response.statusText}`;
  }

  try {
    const parsed = JSON.parse(responseText);

    if (typeof parsed.message === 'string') {
      return parsed.message;
    }

    if (typeof parsed.error === 'string') {
      return parsed.error;
    }

    if (typeof parsed.detail === 'string') {
      return parsed.detail;
    }

    if (Array.isArray(parsed.errors)) {
      return parsed.errors.join(', ');
    }

    if (typeof parsed.errors === 'object' && parsed.errors !== null) {
      return Object.values(parsed.errors).join(', ');
    }

    return responseText;
  } catch {
    return responseText;
  }
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Logged-in user state shared across the app.
  // It stores auth tokens together with the current user's profile details.
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
      // Load the current logged-in user's profile from the backend
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

      if (!response.ok) {
        return false;
      }

      const data: AuthResponse = await response.json();
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
  ): Promise<RegisterResult> => {
    try {
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

      if (!response.ok) {
        const message = await extractErrorMessage(response);

        return {
          success: false,
          message
        };
      }

      return {
        success: true
      };
    } catch (error) {
      console.error('Registration failed', error);

      return {
        success: false,
        message: 'Unable to connect to the server. Please try again.'
      };
    }
  };

  const updateMyProfile = async (request: UserUpdateRequest): Promise<boolean> => {
    if (!user?.accessToken) {
      return false;
    }

    try {
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
    if (!user?.accessToken) {
      return false;
    }

    try {
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

export const useAuth = (): AuthState => {
  const context = useContext(AuthContext);

  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
};