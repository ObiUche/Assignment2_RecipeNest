import React from 'react';

interface PageContainerProps {
  children: React.ReactNode;
}

export const PageContainer: React.FC<PageContainerProps> = ({ children }) => {
  const styles = {
    container: {
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '24px 16px',
      minHeight: 'calc(100vh - 72px)' // Account for navbar
    }
  };

  return <div style={styles.container}>{children}</div>;
};