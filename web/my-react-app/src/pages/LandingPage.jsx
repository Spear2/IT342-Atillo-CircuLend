import React from "react";
import { Link } from "react-router-dom";
import { getToken } from "../security/auth";
import "../css/landing.css";

const LandingPage = () => {
  const isLoggedIn = getToken();

  return (
    <div className="landing-container">
      {/* Optional: A subtle Navigation Bar for a formal feel */}
      <nav className="landing-nav">
        <div className="nav-logo">Auth System</div>
      </nav>

      {/* Main Hero Section */}
      <main className="landing-hero">
        <div className="hero-content">
          <h1 className="hero-title">Testing Environment</h1>
          <p className="hero-subtitle">
            Welcome to my testing environment, please login to access the dashboard or register a new account.
          </p>

          <div className="hero-actions">

            
            
            {isLoggedIn ? (
              <Link to="/dashboard" className="btn btn-primary">
                Access Dashboard
              </Link>
            ) : (
              <>
                <Link to="/login" className="btn btn-primary">
                  Login
                </Link>
                <Link to="/register" className="btn btn-secondary">
                  Create Account
                </Link>
              </>
            )}
          </div>
        </div>
      </main>
      
      <footer className="landing-footer">
        Atillo, Ronel C. - BSIT 3
      </footer>
    </div>
  );
};

export default LandingPage;