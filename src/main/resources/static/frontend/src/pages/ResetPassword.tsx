import React, { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';

// Keep the backend base URL local to this page for now.
// This matches your Spring Boot auth routes under /api/v1/auth.
const API_BASE_URL = 'http://localhost:8080/api/v1';

export const ResetPassword: React.FC = () => {
  // Read the reset token from the URL query string, for example:
  // /reset-password?token=abc123
  const [searchParams] = useSearchParams();

  // Router navigation is used to send the user back to login after success.
  const navigate = useNavigate();

  // Keep local form state for the password reset inputs.
  const [formData, setFormData] = useState({
    newPassword: '',
    confirmPassword: ''
  });

  // Small page state for request progress and user feedback.
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // Pull the token once from the query string.
  const token = searchParams.get('token');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Clear previous messages before validating or sending the request.
    setError('');
    setSuccessMessage('');

    // Stop the flow early if the token is missing from the URL.
    if (!token) {
      setError('Reset token is missing from the URL');
      return;
    }

    // Stop obvious mismatch before making the backend request.
    if (formData.newPassword !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);

    try {
      // Call the real backend reset-password endpoint.
      // The backend expects JSON because the controller uses @RequestBody.
      const response = await fetch(`${API_BASE_URL}/auth/reset-password`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          token,
          newPassword: formData.newPassword
        })
      });

      if (!response.ok) {
        throw new Error('Failed to reset password');
      }

      // Show success feedback first so the user knows the request worked.
      setSuccessMessage('Password reset successful. Redirecting to login...');

      // Give a brief moment for the success message, then return to login.
      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Password reset failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '400px', margin: '48px auto' }}>
        <Card>
          <h1 style={{ textAlign: 'center', marginBottom: '24px' }}>Reset Password</h1>

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

          {successMessage && (
            <div
              style={{
                backgroundColor: '#DCFCE7',
                color: '#166534',
                padding: '12px',
                borderRadius: '8px',
                marginBottom: '16px'
              }}
            >
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <Input
              label="New Password"
              type="password"
              value={formData.newPassword}
              onChange={(e) =>
                setFormData({ ...formData, newPassword: e.target.value })
              }
              required
            />

            <Input
              label="Confirm New Password"
              type="password"
              value={formData.confirmPassword}
              onChange={(e) =>
                setFormData({ ...formData, confirmPassword: e.target.value })
              }
              required
            />

            <Button type="submit" fullWidth disabled={loading}>
              {loading ? 'Resetting...' : 'Reset Password'}
            </Button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '16px' }}>
            Back to <Link to="/login">Login</Link>
          </p>
        </Card>
      </div>
    </PageContainer>
  );
};