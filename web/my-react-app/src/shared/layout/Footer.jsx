import React from "react";
import { Link } from "react-router-dom";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer__inner">
        <nav className="site-footer__links" aria-label="Footer">
          <Link to="/">About</Link>
          <Link to="/">FAQs</Link>
          <Link to="/">Privacy Policy</Link>
        </nav>
        <p className="site-footer__copy">© {new Date().getFullYear()} CircuLend. All rights reserved.</p>
      </div>
    </footer>
  );
}