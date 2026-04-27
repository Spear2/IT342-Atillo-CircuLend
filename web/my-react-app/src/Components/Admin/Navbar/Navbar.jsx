import React, { useEffect, useRef, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import * as auth from "../../../security/auth";
import logo from "../../../assets/logo.png"
import admin from "../../../assets/admin.png"
import down_chevron from "../../../assets/down-chevron.png"
import "./Navbar.css";

const ICONS = {
  logo: logo,
  search: "/placeholders/icon-search.png",
  user: admin,
  chevron: down_chevron,
};

const Navbar = () => {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const navigate = useNavigate();
  const dropdownRef = useRef(null);

  const handleLogout = () => {
    auth.logout();
    navigate("/login");
  };

  useEffect(() => {
    const onClickOutside = (event) => {
      if (!dropdownRef.current) return;
      if (!dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };

    const onEsc = (event) => {
      if (event.key === "Escape") setDropdownOpen(false);
    };

    document.addEventListener("mousedown", onClickOutside);
    document.addEventListener("keydown", onEsc);
    return () => {
      document.removeEventListener("mousedown", onClickOutside);
      document.removeEventListener("keydown", onEsc);
    };
  }, []);

  return (
    <nav className="admin-navbar">
      <div className="nav-left">
        <div className="logo-section">
          <img className="nav-icon nav-logo" src={ICONS.logo} alt="CircuLend logo" />
          <span className="logo-text">CircuLend</span>
        </div>

        
      </div>

      <div className="nav-right">
        <div className="nav-links">
          <NavLink to="/admin" end className={({ isActive }) => (isActive ? "active" : "")}>
            Dashboard
          </NavLink>
          <NavLink to="/admin/*" className={({ isActive }) => (isActive ? "active" : "")}>
            Item Page
          </NavLink>
        </div>

        <div className="admin-profile-section" ref={dropdownRef}>
          <button
            className="admin-dropdown-btn"
            type="button"
            onClick={() => setDropdownOpen((prev) => !prev)}
          >
            <img className="nav-icon nav-user" src={ICONS.user} alt="Admin" />
            <span>Admin</span>
            <img className="nav-icon nav-chevron" src={ICONS.chevron} alt="Open menu" />
          </button>

          {dropdownOpen && (
            <div className="admin-menu">
              <button type="button" onClick={handleLogout} className="logout-btn">
                Logout
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;