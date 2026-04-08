import React from 'react';
import { Card } from './Card';

// Specialized card for statistics
interface StatCardProps {
  label: string;
  value: number | string;
  color?: string;
}

export const StatCard: React.FC<StatCardProps> = ({ label, value, color = '#F97316' }) => {
  return (
    <Card>
      <div style={{ textAlign: 'center' }}>
        <div style={{ 
          fontSize: '14px', 
          color: '#6B7280',
          marginBottom: '8px'
        }}>
          {label}
        </div>
        <div style={{ 
          fontSize: '32px', 
          fontWeight: 'bold',
          color: color
        }}>
          {value}
        </div>
      </div>
    </Card>
  );
};