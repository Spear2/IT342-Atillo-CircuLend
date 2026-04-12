import React from "react";
import { Link } from "react-router-dom";
import { getToken } from "../../security/auth";
import { getDashboardPathForUser } from "../../Utils/navigationPaths";
import Navbar from "../../Components/Shared/Navbar/Navbar";
import Footer from "../../Components/Shared/Footer/Footer";
import heroBackground from "../../assets/Background.jpg";
import imgDiscover from "../../assets/Discover.png";
import imgBorrow from "../../assets/borrow.png";
import imgReturn from "../../assets/return.png";
import imgRealTime from "../../assets/realTime.png";
import imgSecureGoogle from "../../assets/secureGoogle.png";
import imgAutomated from "../../assets/automated.png";
import "./landing.css";

function HeroIllustration() {
  return (
    <div className="landing-hero__figure">
      <img
        src={heroBackground}
        alt=""
        className="landing-hero__img"
        width={560}
        height={420}
        decoding="async"
      />
    </div>
  );
}

export default function LandingPage() {
  const loggedIn = Boolean(getToken());
  const getStartedTo = loggedIn ? getDashboardPathForUser() : "/register";

  return (
    <div className="landing-page">
      <Navbar />

      <main className="landing-main">
        <section className="landing-hero" aria-labelledby="landing-hero-title">
          <div className="landing-hero__grid">
            <div className="landing-hero__art">
              <HeroIllustration />
            </div>
            <div className="landing-hero__copy">
              <h1 id="landing-hero-title" className="landing-hero__title">
                Borrow what you need, share what you have.
              </h1>
              <p className="landing-hero__subtitle">
                The campus-wide equipment sharing platform for students and faculty.
              </p>
              <Link to={getStartedTo} className="landing-btn landing-btn--primary">
                Get Started
              </Link>
            </div>
          </div>
        </section>

        <section className="landing-panel" aria-labelledby="landing-how-heading">
          <h2 id="landing-how-heading" className="landing-panel__title">
            How it Works
          </h2>
          <div className="landing-steps">
            <article className="landing-step">
              <div className="landing-step__figure">
                <img src={imgDiscover} alt="" className="landing-step__img" width={160} height={160} />
              </div>
              <h3 className="landing-step__title">Discover</h3>
              <p className="landing-step__text">
                Browse the catalog for cameras, sports gear, electronics, and more.
              </p>
            </article>
            <article className="landing-step">
              <div className="landing-step__figure">
                <img src={imgBorrow} alt="" className="landing-step__img" width={160} height={160} />
              </div>
              <h3 className="landing-step__title">Borrow</h3>
              <p className="landing-step__text">
                Request what you need with quick confirmation by email.
              </p>
            </article>
            <article className="landing-step">
              <div className="landing-step__figure">
                <img src={imgReturn} alt="" className="landing-step__img" width={160} height={160} />
              </div>
              <h3 className="landing-step__title">Return</h3>
              <p className="landing-step__text">
                Return items on time so the next person can borrow them.
              </p>
            </article>
          </div>
        </section>

        <section className="landing-panel" aria-labelledby="landing-features-heading">
          <h2 id="landing-features-heading" className="landing-panel__title">
            Feature Highlights
          </h2>
          <div className="landing-features">
            <article className="landing-feature">
              <div className="landing-feature__figure">
                <img src={imgRealTime} alt="" className="landing-feature__img" width={120} height={120} />
              </div>
              <h3 className="landing-feature__title">Real-time availability</h3>
              <p className="landing-feature__text">
                See what&apos;s ready to borrow before you commit.
              </p>
            </article>
            <article className="landing-feature">
              <div className="landing-feature__figure">
                <img src={imgSecureGoogle} alt="" className="landing-feature__img" width={120} height={120} />
              </div>
              <h3 className="landing-feature__title">Secure access</h3>
              <p className="landing-feature__text">
                Sign in with your account for safe, authenticated access.
              </p>
            </article>
            <article className="landing-feature">
              <div className="landing-feature__figure">
                <img src={imgAutomated} alt="" className="landing-feature__img" width={120} height={120} />
              </div>
              <h3 className="landing-feature__title">Email notifications</h3>
              <p className="landing-feature__text">
                Get updates for borrowing, returns, and reminders via SMTP.
              </p>
            </article>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}