import React, { useEffect, useState } from 'react';
import { PageContainer } from '../components/layout/PageContainer';
import { StatCard } from '../components/common/StatCard';
import { Card } from '../components/common/Card';
import { useAuth } from '../contexts/AuthContext';
import { AdminDashboard } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const SupervisorPanel: React.FC = () => {
  // Read the logged-in user so the admin token can be sent to the backend.
  const { user } = useAuth();

  // Store the real admin dashboard payload returned by the backend.
  const [dashboard, setDashboard] = useState<AdminDashboard | null>(null);

  // Simple UI state for loading and error feedback.
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboard = async () => {
      // Admin dashboard is a protected route, so an access token is required.
      if (!user?.accessToken) {
        setError('You must be logged in as an admin');
        setLoading(false);
        return;
      }

      try {
        const response = await fetch(`${API_BASE_URL}/admin/dashboard`, {
          headers: {
            Authorization: `Bearer ${user.accessToken}`
          }
        });

        if (!response.ok) {
          throw new Error('Failed to load admin dashboard');
        }

        const data: AdminDashboard = await response.json();
        setDashboard(data);
      } catch (err) {
        console.error(err);
        setError(err instanceof Error ? err.message : 'Failed to load dashboard');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, [user]);

  if (loading) {
    return (
      <PageContainer>
        {/* Keep loading state simple while the admin data is being fetched */}
        <div>Loading admin dashboard...</div>
      </PageContainer>
    );
  }

  if (error || !dashboard) {
    return (
      <PageContainer>
        {/* Show a basic error state if the admin request fails */}
        <div>{error || 'Admin dashboard unavailable'}</div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <h1 style={{ fontSize: '32px', marginBottom: '24px' }}>
        Admin Dashboard
      </h1>

      {/* Main admin stats driven by the real backend dashboard response */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '20px',
          marginBottom: '32px'
        }}
      >
        <StatCard label="Total Users" value={dashboard.totalUsers} />
        <StatCard label="Total Chefs" value={dashboard.totalChefs} />
        <StatCard label="Public Users" value={dashboard.totalPublicUsers} />
        <StatCard label="Admins" value={dashboard.totalAdmins} />
        <StatCard label="Verified Users" value={dashboard.totalVerifiedUsers} />
        <StatCard label="Unverified Users" value={dashboard.totalUnverifiedUsers} />
        <StatCard label="Locked Users" value={dashboard.totalLockedUsers} />
        <StatCard label="Total Recipes" value={dashboard.totalRecipes} />
      </div>

      {/* Recent users table from backend admin data */}
      <Card>
        <div style={{ padding: '24px' }}>
          <h2 style={{ fontSize: '20px', marginBottom: '16px' }}>Recent Users</h2>

          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #E5E7EB' }}>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Name</th>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Email</th>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Role</th>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Verified</th>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Joined</th>
                </tr>
              </thead>
              <tbody>
                {dashboard.recentUsers.map((recentUser) => (
                  <tr key={recentUser.id} style={{ borderBottom: '1px solid #E5E7EB' }}>
                    <td style={{ padding: '12px' }}>{recentUser.fullName}</td>
                    <td style={{ padding: '12px' }}>{recentUser.email}</td>
                    <td style={{ padding: '12px' }}>{recentUser.role}</td>
                    <td style={{ padding: '12px' }}>
                      {recentUser.emailVerified ? 'Yes' : 'No'}
                    </td>
                    <td style={{ padding: '12px', color: '#6B7280' }}>
                      {new Date(recentUser.joinDate).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </Card>

      {/* Recent recipes table from backend admin data */}
      <Card>
        <div style={{ padding: '24px' }}>
          <h2 style={{ fontSize: '20px', marginBottom: '16px' }}>Recent Recipes</h2>

          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #E5E7EB' }}>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Title</th>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Chef</th>
                  <th style={{ textAlign: 'left', padding: '12px' }}>Created</th>
                </tr>
              </thead>
              <tbody>
                {dashboard.recentRecipes.map((recentRecipe) => (
                  <tr key={recentRecipe.id} style={{ borderBottom: '1px solid #E5E7EB' }}>
                    <td style={{ padding: '12px' }}>{recentRecipe.title}</td>
                    <td style={{ padding: '12px' }}>{recentRecipe.chefName ?? 'Unknown chef'}</td>
                    <td style={{ padding: '12px', color: '#6B7280' }}>
                      {new Date(recentRecipe.createdDate).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </Card>
    </PageContainer>
  );
};