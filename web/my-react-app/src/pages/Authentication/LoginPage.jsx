import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./auth.css"; // Dedicated CSS for the login page
import authImg from '../../assets/Background.jpg'
import Navbar from "../../Components/Shared/Navbar/Navbar"
import { setToken, setRole } from "../../security/auth"
import Footer from "../../Components/Shared/Footer/Footer"
import { getApiClient } from "../../api/ApiClientSingleton";


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


  const handleGoogleLogin = () => {
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
  };

  const handleChange = (event) => {
      const { name, value } = event.target;
      setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    if (!form.email || !form.password) {
      setError("Please fill in both email and password.");
      setIsSubmitting(false);
      return;
    }

    try {
      const response = await getApiClient().request("/api/auth/login", {
        method: "POST",
        auth: false, 
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: form.email.trim(),
          password: form.password,
        }),
      });
      const data = await response.json();
      console.log("Login response:", data);
      const token = data?.data?.accessToken;
      const role = data?.data?.user?.role || "BORROWER";

      setToken(token);
      setRole(role);

      if (role.toUpperCase() === "ADMIN") {
        navigate("/admin", { replace: true });
      } else {
        navigate("/borrower", { replace: true });
      }

    } catch (err) {

      if (err.code === "AUTH-004") {
        setError("Email not verified. Please check your email.");
      } else {
        setError(err.message);
      }

    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <Navbar/>

      <main className="auth-main">
        {/* Main Content Split */}
        <div className="content-wrapper">
          
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

              <button type="button" className="btn-google" onClick={handleGoogleLogin}>
                <span className="google-icon">G</span> Sign in with Google
              </button>

              <div className="footer-links">
                <p>Don't have an account? <Link to="/register" className="link-highlight">Sign up</Link></p>
              </div>
            </form>
          </div>
        </div>
      </main>

      <Footer/>
    </div>
  );
};

export default LoginPage;