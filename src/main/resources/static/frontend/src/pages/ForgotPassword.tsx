import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';

// Keep the backend base URL in one place for this page.
// This matches the Spring Boot auth routes under /api/v1/auth.
const API_BASE_URL = 'http://localhost:8080/api/v1';

export const ForgotPassword: React.FC = () => {
  // Store the email the user wants to reset.
  const [email, setEmail] = useState('');

  // Keep simple UI state for loading, errors, and success feedback.
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Clear previous UI messages before sending a new request.
    setLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      // Call the real backend forgot-password endpoint.
      // The backend expects JSON because the controller uses @RequestBody.
      const response = await fetch(`${API_BASE_URL}/auth/forgot-password`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email
        })
      });

      if (!response.ok) {
        throw new Error('Failed to send password reset email');
      }

      // Show success feedback if the backend accepted the request.
      setSuccessMessage('Password reset email sent. Please check your inbox.');
      setEmail('');
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : 'Password reset request failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '400px', margin: '48px auto' }}>
        <Card>
          <h1 style={{ textAlign: 'center', marginBottom: '24px' }}>Forgot Password</h1>

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
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

            <Button type="submit" fullWidth disabled={loading}>
              {loading ? 'Sending...' : 'Send Reset Email'}
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