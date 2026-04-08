import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// Global styles
const globalStyles = {
  '*': {
    margin: 0,
    padding: 0,
    boxSizing: 'border-box'
  },
  body: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, sans-serif',
    backgroundColor: '#F9FAFB',
    color: '#1F2937',
    lineHeight: 1.6
  },
  'h1, h2, h3, h4, h5, h6': {
    color: '#1F2937'
  },
  'a': {
    color: '#F97316',
    textDecoration: 'none'
  }
};

// Inject global styles
const style = document.createElement('style');
style.textContent = `
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body { 
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, sans-serif;
    background-color: #F9FAFB;
    color: #1F2937;
    line-height: 1.6;
  }
  h1, h2, h3, h4, h5, h6 { color: #1F2937; }
  a { color: #F97316; text-decoration: none; }
`;
document.head.appendChild(style);

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);