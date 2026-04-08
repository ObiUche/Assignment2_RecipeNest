import React from 'react';
import { Link } from 'react-router-dom';

// Single responsibility: render a button with variants
interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  to?: string;
  type?: 'button' | 'submit' | 'reset';
  variant?: 'primary' | 'secondary';
  fullWidth?: boolean;
  disabled?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  onClick,
  to,
  type = 'button',
  variant = 'primary',
  fullWidth = false,
  disabled = false
}) => {
  const baseStyles = {
    padding: '12px 24px',
    borderRadius: '8px',
    border: 'none',
    fontSize: '16px',
    fontWeight: '600',
    cursor: disabled ? 'not-allowed' : 'pointer',
    width: fullWidth ? '100%' : 'auto',
    opacity: disabled ? 0.6 : 1,
    transition: 'background-color 0.3s'
  };

  const variantStyles = {
    primary: {
      backgroundColor: '#F97316',
      color: 'white'
    },
    secondary: {
      backgroundColor: 'transparent',
      color: '#F97316',
      border: '2px solid #F97316'
    }
  };

  const styles = { ...baseStyles, ...variantStyles[variant] };

  if (to) {
    return (
      <Link to={to} style={{ textDecoration: 'none' }}>
        <button style={styles} disabled={disabled}>
          {children}
        </button>
      </Link>
    );
  }

  return (
    <button 
      style={styles} 
      onClick={onClick} 
      type={type}
      disabled={disabled}
    >
      {children}
    </button>
  );
};