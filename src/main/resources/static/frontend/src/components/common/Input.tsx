import React from 'react';

// Single responsibility: render input with label
interface InputProps {
  label: string;
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  placeholder?: string;
  required?: boolean;
  multiline?: boolean;
  rows?: number;
}

export const Input: React.FC<InputProps> = ({
  label,
  type = 'text',
  value,
  onChange,
  placeholder,
  required = false,
  multiline = false,
  rows = 3
}) => {
  const id = `input-${label.replace(/\s+/g, '-').toLowerCase()}`;

  const styles = {
    container: {
      marginBottom: '16px'
    },
    label: {
      display: 'block',
      marginBottom: '8px',
      fontWeight: '600',
      color: '#1F2937'
    },
    input: {
      width: '100%',
      padding: '12px',
      borderRadius: '8px',
      border: '1px solid #D1D5DB',
      fontSize: '16px',
      fontFamily: 'inherit'
    }
  };

  return (
    <div style={styles.container}>
      <label htmlFor={id} style={styles.label}>
        {label} {required && <span style={{ color: '#EF4444' }}>*</span>}
      </label>
      {multiline ? (
        <textarea
          id={id}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          rows={rows}
          style={styles.input}
        />
      ) : (
        <input
          id={id}
          type={type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          style={styles.input}
        />
      )}
    </div>
  );
};