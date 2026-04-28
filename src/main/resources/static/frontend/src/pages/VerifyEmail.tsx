import React, { useEffect, useRef, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const VerifyEmail: React.FC = () => {
  // Read the token from the email link, for example:
  // /verify-email?token=abc123
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  // Prevent React StrictMode from calling the verification request twice in development.
  const hasVerified = useRef(false);

  // Track the page state so the user sees a clear message.
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('Verifying your email...');

  useEffect(() => {
    const verifyEmail = async () => {
      if (hasVerified.current) {
        return;
      }

      hasVerified.current = true;

      if (!token) {
        setStatus('error');
        setMessage('Verification token is missing.');
        return;
      }

      try {
        const response = await fetch(
          `${API_BASE_URL}/auth/verify-email?token=${encodeURIComponent(token)}`
        );

        if (!response.ok) {
          throw new Error('Email verification failed.');
        }

        setStatus('success');
        setMessage('Your email has been verified successfully.');
      } catch (error) {
        console.error(error);
        setStatus('error');
        setMessage('Email verification failed or the link has expired.');
      }
    };

    verifyEmail();
  }, [token]);

  return (
    <PageContainer>
      <h1>Email Verification</h1>

      <p>{message}</p>

      {status === 'success' && (
        <Link to="/login">Go to login</Link>
      )}

      {status === 'error' && (
        <Link to="/register">Back to register</Link>
      )}
    </PageContainer>
  );
};