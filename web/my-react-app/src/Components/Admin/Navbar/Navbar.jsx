import React, { useState } from 'react';
import * as auth from '../../../security/auth';
import { useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    auth.logout();
    navigate('/login');
  };

  return (
    <nav className="admin-navbar">
      <div className="nav-left">
        <div className="logo-section">
          <span className="logo-icon">🔄</span>
          <span className="logo-text">CircuLend</span>
        </div>
        <div className="search-bar">
          <span className="search-icon">🔍</span>
          <input type="text" placeholder="Search items, collections..." />
        </div>
      </div>

      <div className="nav-right">
        <div className="nav-links">
          <a href="/admin">Home</a>
          <a href="/admin/dashboard" className="active">Dashboard</a>
        </div>

        <div className="admin-profile-section">
          <div className="dropdown-container">
            <button 
              className="admin-dropdown-btn" 
              onClick={() => setDropdownOpen(!dropdownOpen)}
            >
              Admin <span className="arrow">▾</span>
            </button>
            
            {dropdownOpen && (
              <div className="admin-menu">
                <button onClick={handleLogout} className="logout-btn">Logout</button>
              </div>
            )}
          </div>

          <div className="admin-icon-group">
            <button className="icon-btn">@</button>
            <button className="icon-btn">✉️</button>
            <button className="icon-btn avatar">😎</button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;