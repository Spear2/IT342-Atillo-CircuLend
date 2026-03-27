import React, { useState } from 'react';
import './Navbar.css';
import * as auth from '../../../security/auth'
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const navigate = useNavigate();

  const handleLogout = () =>{
    auth.logout();
    setDropdownOpen(false);
    navigate('/login');
  }

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <div className="logo">
          <span className="logo-icon">🔄</span>
          <span className="logo-text">CircuLend</span>
        </div>
        <div className="search-container">
          <span className="search-icon">🔍</span>
          <input 
            type="text" 
            placeholder="Search items, collections..." 
            className="search-input"
          />
        </div>
      </div>

      <div className="navbar-right">
        <ul className="nav-links">
          <li><a href="/home">Home</a></li>
          <li><a href="/dashboard" className="active">Dashboard</a></li>
        </ul>

        <div className="user-profile-section">
          <div className='dropdown-wrapper'>
            <div className="user-dropdown" onClick={() => setDropdownOpen(!dropdownOpen)}>
              <span className="user-name">John Doe</span>
              <span className={`arrow ${dropdownOpen ? 'up' : 'down'}`}>▾</span>
            </div>

            {dropdownOpen && (
            <div className="dropdown-menu">
                <button className= "dropdown-item logout" onClick={handleLogout}>
                  logout
                </button>
              </div>
            )}
            
          </div>
          
          <div className="icon-group">
            <button className="nav-icon-btn purple-bg">@</button>
            <button className="nav-icon-btn blue-bg">✉️</button>
            <button className="nav-icon-btn avatar-bg">😎</button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;