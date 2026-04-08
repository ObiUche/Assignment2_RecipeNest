import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';

export const Login: React.FC = () => {
  // Local form state for the login inputs and simple UI feedback.
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Read login action and current auth user from context.
  const { login, user } = useAuth();
  const navigate = useNavigate();

  // Redirect after auth state updates instead of reading user.role too early
  // inside the submit handler before React finishes updating state.
  useEffect(() => {
    if (!user) {
      return;
    }

    if (user.role === 'ADMIN') {
      navigate('/admin');
    } else if (user.role === 'CHEF') {
      navigate('/chef/dashboard');
    } else {
      navigate('/');
    }
  }, [user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Login request should go through auth context.
    // Backend login expects JSON because the controller uses @RequestBody.
    const success = await login(email, password);

    if (!success) {
      setError('Invalid email or password');
    }

    setLoading(false);
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '400px', margin: '48px auto' }}>
        <Card>
          <h1 style={{ textAlign: 'center', marginBottom: '24px' }}>Login</h1>

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
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

            <Input
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <Button type="submit" fullWidth disabled={loading}>
              {loading ? 'Logging in...' : 'Login'}
            </Button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '16px' }}>
            Don&apos;t have an account? <Link to="/register">Register</Link>
          </p>

          <p style={{ textAlign: 'center', marginTop: '12px' }}>
            <Link to="/forgot-password">Forgot your password?</Link>
          </p>
        </Card>
      </div>
    </PageContainer>
  );
};