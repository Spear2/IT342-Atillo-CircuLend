import React, { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import "./auth.css";
import authImg from "../../assets/Background.jpg";
import Navbar from "../../Components/Shared/Navbar/Navbar";
import { setToken, setRole } from "../../security/auth";
import Footer from "../../Components/Shared/Footer/Footer";
import { getApiClient } from "../../api/ApiClientSingleton";

import password from "../../assets/lock.png"
import hidden from "../../assets/hidden.png"
import google from "../../assets/google.png"
import email from "../../assets/email.png"
import arrow from "../../assets/right-arrow.png"
import unhidden from "../../assets/unhidden.png"
import welcome from "../../assets/hello.png"

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const AUTH_ICONS = {
  email: email,
  password: password,
  eyeOpen: hidden,
  eyeClosed: unhidden,
  arrow: arrow,
  google: google,
  welcome: welcome,
};

const LoginPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);

  const [form, setForm] = useState({ email: "", password: "" });

  useEffect(() => {
    const oauth2 = searchParams.get("oauth2");
    const genericError = searchParams.get("error");

    if (oauth2 === "missing_token") {
      setError("Google login failed: token was not returned.");
    } else if (oauth2 === "email_exists_local") {
      setError("This email is already registered with password login. Please sign in using email and password.");
    } else if (oauth2) {
      setError(`Google login failed: ${decodeURIComponent(oauth2)}`);
    } else if (genericError) {
      setError("Google sign-in failed. Please try again.");
    }
  }, [searchParams]);

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

      if (!response.ok || data?.success === false) {
        const code = data?.error?.code;
        const message =
          data?.error?.message || data?.message || "Login failed. Please try again.";
        const err = new Error(message);
        err.code = code;
        throw err;
      }

      const token = data?.data?.accessToken;
      const role = data?.data?.user?.role;

      if (!token || !role) {
        throw new Error("Login response is incomplete. Please try again.");
      }

      setToken(token);
      setRole(role);
      navigate(role.toUpperCase() === "ADMIN" ? "/admin" : "/borrower", { replace: true });
    } catch (err) {
      if (err.code === "AUTH-004") {
        setError("Email not verified. Please check your email.");
      } else if (err.code === "AUTH-007") {
        setError("This account is registered with Google. Please sign in with Google.");
      } else {
        setError(err.message || "Login failed. Please try again.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <Navbar />
      <main className="auth-main">
        <div className="content-wrapper">
          <div className="left-pane">
            <div className="auth-hero-wrap">
              <div className="auth-hero-caption-block">
                <h2 className="auth-hero-heading">
                  Welcome <span>CircuLend</span>
                </h2>
                <p className="auth-hero-subtext">
                  Continue borrowing and sharing equipment with your campus community.
                </p>
              </div>

              <img
                src={authImg}
                alt="CircuLend collaboration"
                className="auth-illustration"
              />
            </div>
          </div>

          <div className="right-pane" style={{marginTop:"60px"}}>
            <form className="form-container" onSubmit={handleSubmit}>
              
                <div className="title-row">
                  <h2 className="form-title">Welcome back</h2>
                  <img className="welcome-icon" src={AUTH_ICONS.welcome} alt="Welcome" />
                </div>
              
              <p className="form-subtitle">Log in your account</p>

              <div className="form-group">
                <div className="input-wrapper">
                  <img className="input-icon-img" src={AUTH_ICONS.email} alt="Email" />
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
                  <img className="input-icon-img" src={AUTH_ICONS.password} alt="Password" />
                  <input
                    id="password"
                    name="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="Enter password"
                    value={form.password}
                    onChange={handleChange}
                    className="input-field has-right-btn"
                    required
                  />
                  <button
                    type="button"
                    className="password-toggle-btn"
                    onClick={() => setShowPassword((prev) => !prev)}
                    aria-label={showPassword ? "Hide password" : "Show password"}
                  >
                    <img
                      className="toggle-icon-img"
                      src={showPassword ? AUTH_ICONS.eyeClosed : AUTH_ICONS.eyeOpen}
                      alt={showPassword ? "Hide" : "Show"}
                    />
                  </button>
                </div>
              </div>

              <div className="forgot-password-container">
                <Link to="/forgot-password" className="link-highlight text-sm">Forgot password?</Link>
              </div>

              {error && <div className="error-message">{error}</div>}

              <button className="btn-submit" type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Logging in..." : "Continue"}
                <img className="arrow-icon-img" src={AUTH_ICONS.arrow} alt="" />
              </button>

              <div className="divider"><span>OR</span></div>

              <button type="button" className="btn-google" onClick={handleGoogleLogin}>
                <img className="google-btn-icon-img" src={AUTH_ICONS.google} alt="Google" />
                Sign in with Google
              </button>

              <div className="footer-links">
                <p>Don't have an account? <Link to="/register" className="link-highlight">Sign up</Link></p>
              </div>
            </form>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default LoginPage;