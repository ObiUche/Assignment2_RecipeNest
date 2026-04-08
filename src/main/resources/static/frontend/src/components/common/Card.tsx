import React from 'react';

// Pure presentation component
interface CardProps {
  children: React.ReactNode;
  padding?: string;
}

export const Card: React.FC<CardProps> = ({ children, padding = '20px' }) => {
  const styles = {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding,
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    height: '100%'
  };

  return <div style={styles}>{children}</div>;
};