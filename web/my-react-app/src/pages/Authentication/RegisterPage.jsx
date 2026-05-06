
import React, { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import "./auth.css";
import authImgs from "../../assets/Background.jpg";
import Navbar from "../../Components/Shared/Navbar/Navbar";
import Footer from "../../Components/Shared/Footer/Footer";
import { getApiClient } from "../../api/ApiClientSingleton";

import password from "../../assets/lock.png";
import hidden from "../../assets/hidden.png";
import google from "../../assets/google.png";
import email from "../../assets/email.png";
import arrow from "../../assets/right-arrow.png";
import unhidden from "../../assets/unhidden.png";
import welcome from "../../assets/hello.png";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const AUTH_ICONS = {
  email,
  password,
  eyeOpen: hidden,
  eyeClosed: unhidden,
  arrow,
  google,
  welcome,
};

const RegisterPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

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

  useEffect(() => {
    const oauth2 = searchParams.get("oauth2");
    const genericError = searchParams.get("error");

    if (oauth2 === "missing_token") {
      setError("Google sign-up failed: token was not returned.");
    } else if (oauth2) {
      setError(`Google sign-up failed: ${decodeURIComponent(oauth2)}`);
    } else if (genericError) {
      setError("Google sign-up failed. Please try again.");
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
      const response = await getApiClient().request("/api/auth/register", {
        method: "POST",
        auth: false,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          firstName: form.firstName.trim(),
          lastName: form.lastName.trim(),
          email: form.email.trim().toLowerCase(),
          password: form.password,
        }),
      });

      const data = await response.json();

      if (!response.ok || data?.success === false) {
        const err = new Error(
          data?.error?.message || data?.message || "Registration failed. Please try again."
        );
        err.code = data?.error?.code;
        throw err;
      }

      alert("Registration successful! Check your email to verify your account.");
      navigate("/login");
    } catch (err) {
      if (err.code === "AUTH-005") {
        setError("Email already exists. Please log in instead.");
      } else {
        setError(err.message || "Registration failed. Please try again.");
      }
    }
  };

  return (
    <div className="register-page">
      <Navbar />

      <main className="auth-main">
        <div className="content-wrapper">
          <div className="left-pane">
            <div className="auth-hero-wrap">
              <div className="auth-hero-caption-block">
                <h2 className="auth-hero-heading">
                  Join <span>CircuLend</span>
                </h2>
                <p className="auth-hero-subtext">
                  Join a sustainable community sharing equipment and resources.
                </p>
              </div>

              <img
                src={authImgs}
                alt="CircuLend collaboration"
                className="auth-illustration"
              />
            </div>
          </div>

          <div className="right-pane">
            <form className="form-container" onSubmit={handleSubmit}>
              <div className="title-row">
                <h2 className="form-title">Create an Account</h2>
                <img className="welcome-icon" src={AUTH_ICONS.welcome} alt="Create account" />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="label" htmlFor="firstName">
                    First Name
                  </label>
                  <div className="input-wrapper">
                    <img className="input-icon-img" src={AUTH_ICONS.email} alt="" />
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
                  <label className="label" htmlFor="lastName">
                    Last Name
                  </label>
                  <div className="input-wrapper">
                    <img className="input-icon-img" src={AUTH_ICONS.email} alt="" />
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
                <label className="label" htmlFor="email">
                  Email
                </label>
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
                <label className="label" htmlFor="password">
                  Password
                </label>
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

              <div className="form-group">
                <label className="label" htmlFor="confirmPassword">
                  Confirm Password
                </label>
                <div className="input-wrapper">
                  <img className="input-icon-img" src={AUTH_ICONS.password} alt="Confirm password" />
                  <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type={showConfirmPassword ? "text" : "password"}
                    placeholder="Re-enter password"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    className="input-field has-right-btn"
                    required
                  />
                  <button
                    type="button"
                    className="password-toggle-btn"
                    onClick={() => setShowConfirmPassword((prev) => !prev)}
                    aria-label={showConfirmPassword ? "Hide password" : "Show password"}
                  >
                    <img
                      className="toggle-icon-img"
                      src={showConfirmPassword ? AUTH_ICONS.eyeClosed : AUTH_ICONS.eyeOpen}
                      alt={showConfirmPassword ? "Hide" : "Show"}
                    />
                  </button>
                </div>
              </div>

              {error && <div className="error-message">{error}</div>}

              <button className="btn-submit" type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Creating..." : "Create Account"}
                <img className="arrow-icon-img" src={AUTH_ICONS.arrow} alt="" />
              </button>

              <div className="divider">
                <span>OR</span>
              </div>

              <button type="button" className="btn-google" onClick={handleGoogleLogin}>
                <img className="google-btn-icon-img" src={AUTH_ICONS.google} alt="Google" />
                Sign up with Google
              </button>

              <div className="footer-links">
                <p>
                  Already have an account?{" "}
                  <Link to="/login" className="link-highlight">
                    Sign in
                  </Link>
                </p>
                <Link to="/terms" className="link-highlight terms-link">
                  Terms & Conditions
                </Link>
              </div>
            </form>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default RegisterPage;