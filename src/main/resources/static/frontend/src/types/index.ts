// Backend role values returned by your Spring Boot API.
// Keep these exactly aligned with the backend enum values.
export type UserRole = 'CHEF' | 'PUBLIC' | 'ADMIN';

// One uploaded photo linked to a recipe.
// Used for gallery display, replacing photos, and primary-image selection.
export interface RecipePhoto {
  id: number;
  imageUrl: string;
  caption: string | null;
  isPrimary: boolean;
}

// Small admin-dashboard view of a recent user.
// This is not the full user profile, only the summary needed by admin tables/cards.
export interface RecentUser {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  emailVerified: boolean;
  joinDate: string;
}

// Full user profile returned by GET /api/v1/users/me
// This should match the backend UserResponse DTO.
export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  profilePhoto: string | null;
  location: string | null;
  bio: string | null;
  cuisineSpeciality: string | null;
  role: UserRole;
  joinDate: string;
}

// Payload used for PATCH /api/v1/users/me
// All fields are optional because profile editing is a partial update.
export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  location?: string;
  bio?: string;
  cuisineSpeciality?: string;
}

// Logged-in frontend user state.
// Keep profile fields here so the UI can render real user details.
export interface AuthUser {
  id?: number;
  firstName?: string;
  lastName?: string;
  email: string;
  profilePhoto?: string | null;
  location?: string | null;
  bio?: string | null;
  cuisineSpeciality?: string | null;
  role: UserRole;
  joinDate?: string;
  accessToken?: string;
  refreshToken?: string;
}

// Small admin-dashboard view of a recent recipe.
// This keeps admin pages light and avoids over-fetching full recipe detail data.
export interface RecentRecipe {
  id: number;
  title: string;
  chefName: string | null;
  createdDate: string;
  mainImage: string | null;
}

// Exact auth payload returned by the backend login/refresh endpoints.
// Keep this aligned with the Spring Boot AuthResponse DTO.
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  email: string;
  role: UserRole;
}

// Full recipe shape used across recipe pages.
// This should match your backend RecipeResponse DTO as closely as possible.
export interface Recipe {
  id: number;
  title: string;
  description: string;
  instructions: string;
  cookingTime: number;
  difficulty: string;
  servings: number;
  cuisineType: string;
  mainImage: string | null;
  viewCount: number;
  createdDate: string;
  chefId: number | null;
  chefName: string | null;
  ingredients: string[];
  photos: RecipePhoto[];
}

// Full admin dashboard payload returned by the backend.
// This is the data source for admin stat cards and recent activity sections.
export interface AdminDashboard {
  totalUsers: number;
  totalChefs: number;
  totalPublicUsers: number;
  totalAdmins: number;
  totalVerifiedUsers: number;
  totalUnverifiedUsers: number;
  totalLockedUsers: number;
  totalRecipes: number;
  recentUsers: RecentUser[];
  recentRecipes: RecentRecipe[];
}

// Auth context contract used by the frontend.
// updateMyProfile handles text fields, while uploadProfilePhoto handles multipart image upload.
export interface AuthState {
  user: AuthUser | null;
  login: (email: string, password: string) => Promise<boolean>;
  logout: () => Promise<void>;
  register: (
    firstname: string,
    lastname: string,
    email: string,
    password: string,
    role: UserRole
  ) => Promise<boolean>;
  updateMyProfile?: (request: UserUpdateRequest) => Promise<boolean>;
  uploadProfilePhoto?: (file: File) => Promise<boolean>;
}

// Recipe context contract used by recipe pages.
// getRecipe returns one recipe, while getUserRecipes filters by chef ownership.
export interface RecipeState {
  recipes: Recipe[];
  setRecipes: (recipes: Recipe[]) => void;
  getRecipe: (id: number) => Recipe | undefined;
  getUserRecipes: (chefId: number) => Recipe[];
}

// Public chef profile returned by GET /api/v1/users/{id}/public
// This is used by the public chef profile page.
export interface PublicChefProfile {
  id: number;
  firstName: string;
  lastName: string;
  fullName: string;
  profilePhoto: string | null;
  location: string | null;
  bio: string | null;
  cuisineSpeciality: string | null;
}