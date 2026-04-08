import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';

export const ChangePassword: React.FC = () => {
  // Router navigation is used for cancel/back actions.
  const navigate = useNavigate();

  // Keep local form state for basic client-side validation.
  // This page no longer pretends a mock password update succeeds.
  const [formData, setFormData] = useState({
    current: '',
    newPassword: '',
    confirmPassword: ''
  });

  // Store a small validation message for the user.
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Stop obvious mismatch before doing anything else.
    if (formData.newPassword !== formData.confirmPassword) {
      setError('New passwords do not match');
      return;
    }

    // Be honest: the backend currently supports forgot/reset password flow,
    // but not an authenticated "change password" endpoint from inside the profile page.
    setError('Direct password change is not wired yet. Use the reset-password flow instead.');
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '400px', margin: '48px auto' }}>
        <Card>
          <h1 style={{ fontSize: '24px', marginBottom: '24px' }}>Change Password</h1>

          {error && (
            <div
              style={{
                backgroundColor: '#FEE2E2',
                color: '#DC2626',
                padding: '12px',
                borderRadius: '8px',
                marginBottom: '16px'
              }}
            >
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <Input
              label="Current Password"
              type="password"
              value={formData.current}
              onChange={(e) => setFormData({ ...formData, current: e.target.value })}
              required
            />

            <Input
              label="New Password"
              type="password"
              value={formData.newPassword}
              onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
              required
            />

            <Input
              label="Confirm New Password"
              type="password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              required
            />

            <div style={{ display: 'flex', gap: '16px', marginBottom: '16px' }}>
              {/* Keep the button for now, but do not fake a successful password change */}
              <Button type="submit">Update Password</Button>

              {/* Return to the previous screen without changing anything */}
              <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                Cancel
              </Button>
            </div>
          </form>

          <p style={{ marginTop: '8px' }}>
            Need to reset instead? <Link to="/forgot-password">Go to forgot password</Link>
          </p>
        </Card>
      </div>
    </PageContainer>
  );
};