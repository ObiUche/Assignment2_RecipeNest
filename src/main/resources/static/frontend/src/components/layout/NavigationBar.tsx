import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../common/Button';

export const NavigationBar: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const closeMenu = () => {
    setMenuOpen(false);
  };

  const handleLogout = () => {
    // Clear auth state, return the user to the homepage,
    // and close the mobile menu if it is currently open
    logout();
    navigate('/');
    closeMenu();
  };

  const styles = {
    nav: {
      backgroundColor: 'white',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
      position: 'sticky' as const,
      top: 0,
      zIndex: 1000
    },
    container: {
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '16px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center'
    },
    logo: {
      fontSize: '24px',
      fontWeight: 'bold',
      color: '#F97316',
      textDecoration: 'none'
    },
    mobileMenuBtn: {
      display: 'none',
      background: 'none',
      border: 'none',
      fontSize: '24px',
      cursor: 'pointer',
      '@media (maxWidth: 768px)': {
        display: 'block'
      }
    },
    navLinks: {
      display: 'flex',
      gap: '20px',
      alignItems: 'center',
      '@media (maxWidth: 768px)': {
        display: menuOpen ? 'flex' : 'none',
        flexDirection: 'column' as const,
        position: 'absolute' as const,
        top: '72px',
        left: 0,
        right: 0,
        backgroundColor: 'white',
        padding: '20px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }
    },
    link: {
      color: '#1F2937',
      textDecoration: 'none',
      fontSize: '16px'
    }
  };

  return (
    <nav style={styles.nav}>
      <div style={styles.container}>
        <Link to="/" style={styles.logo} onClick={closeMenu}>
          RecipeNest
        </Link>

        <button
          style={styles.mobileMenuBtn}
          onClick={() => setMenuOpen(!menuOpen)}
        >
          ☰
        </button>

        <div style={styles.navLinks}>
          <Link to="/recipes" style={styles.link} onClick={closeMenu}>
            Recipes
          </Link>

          {user ? (
            <>
              {user.role === 'CHEF' && (
                <Link to="/chef/dashboard" style={styles.link} onClick={closeMenu}>
                  Dashboard
                </Link>
              )}

              {user.role === 'ADMIN' && (
                <Link to="/admin" style={styles.link} onClick={closeMenu}>
                  Admin
                </Link>
              )}

              <Link to="/profile/edit" style={styles.link} onClick={closeMenu}>
                Profile
              </Link>

              <Button onClick={handleLogout}>Logout</Button>
            </>
          ) : (
            <>
              <Link to="/login" style={styles.link} onClick={closeMenu}>
                Login
              </Link>
              <Link to="/register" style={styles.link} onClick={closeMenu}>
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};