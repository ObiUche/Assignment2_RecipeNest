import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import { UserRole } from '../types';

export const Register: React.FC = () => {
  // Keep register form state aligned with the backend request DTO
  const [formData, setFormData] = useState({
    firstname: '',
    lastname: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'PUBLIC' as UserRole
  });

  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Stop obvious password mismatch before making the backend request
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);
    setError('');
    setSuccessMessage('');

    const success = await register(
      formData.firstname,
      formData.lastname,
      formData.email,
      formData.password,
      formData.role
    );

    if (success) {
      // Registration in your backend creates the account but still requires
      // email verification before the user can log in successfully.
      setSuccessMessage('Registration successful. Please verify your email before logging in.');
      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } else {
      setError('Registration failed');
    }

    setLoading(false);
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '400px', margin: '48px auto' }}>
        <Card>
          <h1 style={{ textAlign: 'center', marginBottom: '24px' }}>Register</h1>

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
              label="First Name"
              value={formData.firstname}
              onChange={(e) => setFormData({ ...formData, firstname: e.target.value })}
              required
            />

            <Input
              label="Last Name"
              value={formData.lastname}
              onChange={(e) => setFormData({ ...formData, lastname: e.target.value })}
              required
            />

            <Input
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              required
            />

            <Input
              label="Password"
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
            />

            <Input
              label="Confirm Password"
              type="password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              required
            />

            <div style={{ marginBottom: '16px' }}>
              <label
                htmlFor="role"
                style={{
                  display: 'block',
                  marginBottom: '8px',
                  fontWeight: 500
                }}
              >
                Account Type
              </label>

              <select
                id="role"
                value={formData.role}
                onChange={(e) =>
                  setFormData({ ...formData, role: e.target.value as UserRole })
                }
                style={{
                  width: '100%',
                  padding: '12px',
                  borderRadius: '8px',
                  border: '1px solid #D1D5DB',
                  fontSize: '16px'
                }}
              >
                <option value="PUBLIC">Public</option>
                <option value="CHEF">Chef</option>
              </select>
            </div>

            <Button type="submit" fullWidth disabled={loading}>
              {loading ? 'Registering...' : 'Register'}
            </Button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '16px' }}>
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </Card>
      </div>
    </PageContainer>
  );
};