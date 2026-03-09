import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../css/auth.css"; // Dedicated CSS for the login page
import authImg from "../assets/background.jpg"
import logo from "../assets/logo.png"
import { setToken, setRole } from "../security/auth"

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const LoginPage = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  
  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
  e.preventDefault();
  setIsSubmitting(true);
  setError(null);

  if (!form.email || !form.password) {
    alert("Please fill in both email and password.");
    setIsSubmitting(false);
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: form.email,
        password: form.password
      }),
    });

    const data = await response.json();
    
    console.log("SERVER RESPONSE:", data); 

    if (!response.ok) {
      alert(data.error?.message || "Invalid email or password.");
      return;
    }

    const token = data.data?.accessToken;
    const role = data.data?.user?.role || "BORROWER";

    if (!token) {
      throw new Error("Token missing from server response!");
    }

    setToken(token);
    setRole(role);

    alert("Login successful!");

    if (role.toUpperCase() === "ADMIN") {
      navigate("/dashboard", { replace: true });
    } else {
      navigate("/dashboard", { replace: true });
    }

  } catch (err) {
    setError(err.message || "System unreachable. Please try again later.");
  } finally {
    setIsSubmitting(false);
  }
};

  return (
    <div className="login-page">
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

      {/* Main Content Split */}
      <div className="content-wrapper" style={{marginTop:"100px"}}>
        
        {/* Left Side: Completely blank per the design */}
        <div className="left-pane">
          <img 
            src={authImg}
            alt="CircuLend Collaboration" 
            className="auth-illustration"
          />
        </div>

        {/* Right Side: Floating Form */}
        <div className="right-pane">
          <form className="form-container" onSubmit={handleSubmit}>
            
            <h2 className="form-title">Welcome back 👋</h2>
            <p className="form-subtitle">Log in your account</p>

            <div className="form-group">
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
              <div className="input-wrapper">
                <span className="input-icon"></span>
                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="Hellojrobinson@123"
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

            <div className="forgot-password-container">
              <Link to="/forgot-password" className="link-highlight text-sm">Forgot password?</Link>
            </div>

            {error && <div className="error-message">{error}</div>}

            <button className="btn-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Logging in..." : "Continue"}
            </button>

            <div className="divider">
              <span>OR</span>
            </div>

            <button type="button" className="btn-google">
              <span className="google-icon">G</span> Continue with Google
            </button>

            <div className="footer-links">
              <p>Don't have an account? <Link to="/register" className="link-highlight">Sign up</Link></p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;