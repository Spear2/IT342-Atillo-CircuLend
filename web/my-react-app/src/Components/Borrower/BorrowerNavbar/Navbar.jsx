import React, { useEffect, useRef, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import * as auth from "../../../security/auth";
import logo from "../../../assets/logo.png";
import userIcon from "../../../assets/user.png"; // replace with borrower icon if you have one
import chevronDown from "../../../assets/down-chevron.png";
import "./Navbar.css";

const Navbar = () => {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    auth.logout();
    setDropdownOpen(false);
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
    <nav className="borrower-navbar">
      <div className="borrower-nav-left">
        <div className="borrower-logo-section">
          <img className="borrower-nav-icon borrower-nav-logo" src={logo} alt="CircuLend logo" />
          <span className="borrower-logo-text">CircuLend</span>
        </div>
      </div>

      <div className="borrower-nav-right">
        <div className="borrower-nav-links">
          <NavLink
            to="/borrower"
            end
            className={({ isActive }) => (isActive ? "active" : "")}
          >
            Home
          </NavLink>

          <NavLink
            to="/borrower/dashboard"
            className={({ isActive }) => (isActive ? "active" : "")}
          >
            Dashboard
          </NavLink>
        </div>

        <div className="borrower-profile-section" ref={dropdownRef}>
          <button
            className="borrower-dropdown-btn"
            type="button"
            onClick={() => setDropdownOpen((prev) => !prev)}
          >
            <img className="borrower-nav-icon borrower-nav-user" src={userIcon} alt="Borrower" />
            <span>Borrower</span>
            <img className="borrower-nav-icon borrower-nav-chevron" src={chevronDown} alt="Open menu" />
          </button>

          {dropdownOpen && (
            <div className="borrower-menu">
              <button type="button" className="borrower-logout-btn" onClick={handleLogout}>
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