import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./auth.css"; // Dedicated CSS for the login page
import authImgs from "../../assets/Background.jpg"
import logo from "../../assets/Logo.png"
import Footer from "../../Components/Shared/Footer/Footer"
import { apiFetch } from "../../Utils/apiFetch";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const RegisterPage = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };


  

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    if (!form.firstName.trim()) {
      setError("First name is required.");
      setIsSubmitting(false);
      return;
    }

    if (!form.lastName.trim()) {
      setError("Last name is required.");
      setIsSubmitting(false);
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(form.email)) {
      setError("Please enter a valid email address.");
      setIsSubmitting(false);
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      setIsSubmitting(false);
      return;
    }

    try {
      await apiFetch(`${API_BASE}/api/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(form)
      });

      alert("Registration successful! Check your email to verify your account.");
      navigate("/login");

    } catch (err) {
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="register-page">
      <header className="header">
        
        <div className="brand-container">
          <img src={logo} alt="CircuLend Logo" className="logo-illustration"/>
          <div className="brand-logo-text">CircuLend</div>
        </div>

        <div className="header-icons">
          <span className="icon"></span>
          <span className="icon dark-circle"></span>
        </div>
      </header>


      <div className="content-wrapper">
        
        {/* Left Side: Completely blank per the design */}
                <div className="left-pane">
                  <img 
                    src={authImgs}
                    alt="CircuLend Collaboration" 
                    className="auth-illustration"
                  />
                </div>

        {/* Right Side: Floating Form */}
        <div className="right-pane">
          <form className="form-container" onSubmit={handleSubmit}>
            
            <h2 className="form-title">Create an Account</h2>

            <div className="form-row">
              <div className="form-group">
                <label className="label" htmlFor="firstName">First Name</label>
                <div className="input-wrapper">
                  <span className="input-icon"></span> 
                  <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    placeholder="John"
                    value={form.firstName}
                    onChange={handleChange}
                    className="input-field"
                    required
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label className="label" htmlFor="lastName">Last Name</label>
                <div className="input-wrapper">
                  <span className="input-icon"></span>
                  <input
                    id="lastName"
                    name="lastName"
                    type="text"
                    placeholder="Doe"
                    value={form.lastName}
                    onChange={handleChange}
                    className="input-field"
                    required
                  />
                </div>
              </div>
            </div>

            <div className="form-group">
              <label className="label" htmlFor="email">Email</label>
              <div className="input-wrapper">
                <span className="input-icon"></span>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="jrobinson@hotmail.com"
                  value={form.email}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="label" htmlFor="password">Password</label>
              <div className="input-wrapper">
                <span className="input-icon"></span>
                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="HelloJrobinson@123"
                  value={form.password}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
                <span 
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? "" : ""}
                </span>
              </div>
            </div>

            <div className="form-group">
              <label className="label" htmlFor="confirmPassword">Confirm Password</label>
              <div className="input-wrapper">
                <span className="input-icon"></span>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  placeholder="HelloJrobinson@123"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
                <span 
                  className="password-toggle"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                >
                  {showConfirmPassword ? "" : ""}
                </span>
              </div>
            </div>

            {error && <div className="error-message">{error}</div>}

            <button className="btn-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Creating..." : "Create Account"}
            </button>

            <div className="divider">
              <span>OR</span>
            </div>

            <button type="button" className="btn-google">
              <span className="google-icon">G</span> Continue with Google
            </button>

            <div className="footer-links">
              <p>Already have an account? <Link to="/login" className="link-highlight">Sign in</Link></p>
              <Link to="/terms" className="link-highlight terms-link">Terms & Conditions</Link>
            </div>
          </form>
        </div>
      </div>
      
    </div>
  );
};

export default RegisterPage;