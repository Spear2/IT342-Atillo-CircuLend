import React from "react";
import { Link } from "react-router-dom";
import { getToken } from "../../../security/auth";
import { getDashboardPathForUser } from "../../../Utils/navigationPaths";
import logo from "../../../assets/Logo.png";
import "./Navbar.css";

export default function Navbar() {
  const loggedIn = Boolean(getToken());
  const dashboardTo = getDashboardPathForUser();

  return (
    <header className="shared-navbar" role="banner">
      <Link to="/" className="shared-navbar__brand" aria-label="CircuLend home">
        <img src={logo} alt="" className="shared-navbar__logo" width={40} height={40} />
        <span className="shared-navbar__title">
          <span className="shared-navbar__title-circu">Circu</span>
          <span className="shared-navbar__title-lend">Lend</span>
        </span>
      </Link>

      <nav className="shared-navbar__actions" aria-label="Account">
        {loggedIn ? (
          <Link to={dashboardTo} className="shared-navbar__btn shared-navbar__btn--solid">
            Dashboard
          </Link>
        ) : (
          <>
            <Link to="/login" className="shared-navbar__btn shared-navbar__btn--outline">
              Login
            </Link>
            <Link to="/register" className="shared-navbar__btn shared-navbar__btn--solid">
              Sign Up
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}