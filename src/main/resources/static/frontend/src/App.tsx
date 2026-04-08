import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { RecipeProvider } from './contexts/RecipeContext';
import { NavigationBar } from './components/layout/NavigationBar';
import { UserRole } from './types';

// Import pages
import { Homepage } from './pages/HomePage';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ForgotPassword } from './pages/ForgotPassword';
import { ResetPassword } from './pages/ResetPassword';
import { ViewRecipes } from './pages/ViewRecipes';
import { ChefProfile } from './pages/ChefProfile';
import { ChefDashboard } from './pages/ChefDashboard';
import { EditRecipe } from './pages/EditRecipe';
import { EditChefProfile } from './pages/EditChefProfile';
import { ChangePassword } from './pages/ChangePassword';
import { SupervisorPanel } from './pages/SupervisorPanel';
import { RecipeDetail } from './pages/RecipeDetail';

// Protected route wrapper used for pages that require login,
// and optionally a specific backend role such as CHEF or ADMIN.
const ProtectedRoute: React.FC<{
  children: React.ReactNode;
  requiredRole?: UserRole;
}> = ({ children, requiredRole }) => {
  const { user } = useAuth();

  // Redirect unauthenticated users to the login page first.
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Redirect users who are logged in but do not have the required role.
  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

function AppContent() {
  return (
    <>
      <NavigationBar />
      <Routes>
        {/* Public routes available to everyone */}
        <Route path="/" element={<Homepage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/recipes" element={<ViewRecipes />} />
        <Route path="/chef/:id" element={<ChefProfile />} />
        <Route path="/recipes/:id" element = {<RecipeDetail />}/>

        {/* Protected routes for chefs who manage recipes */}
        <Route
          path="/chef/dashboard"
          element={
            <ProtectedRoute requiredRole="CHEF">
              <ChefDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/recipe/new"
          element={
            <ProtectedRoute requiredRole="CHEF">
              <EditRecipe />
            </ProtectedRoute>
          }
        />
        <Route
          path="/recipe/edit/:id"
          element={
            <ProtectedRoute requiredRole="CHEF">
              <EditRecipe />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile/edit"
          element={
            <ProtectedRoute>
              <EditChefProfile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/change-password"
          element={
            <ProtectedRoute>
              <ChangePassword />
            </ProtectedRoute>
          }
        />

        {/* Admin-only dashboard route */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <SupervisorPanel />
            </ProtectedRoute>
          }
        />
      </Routes>
    </>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <RecipeProvider>
          <AppContent />
        </RecipeProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;