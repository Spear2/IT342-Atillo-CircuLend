import React from 'react';
import './Footer.css'
const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-links">
        <span>About</span> |
        <span>FAQs</span> |
        <span>Support</span> |
        <span>Terms</span> |
        <span>Privacy</span>
      </div>
      <div>© 2022 CircuLend • All rights reserved.</div>
    </footer>
  );
};

export default Footer;