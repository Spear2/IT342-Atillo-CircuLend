import React, { useEffect, useState, useRef } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { apiFetch } from "@/shared/utils/apiFetch";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const hasVerifiedRef = useRef(false);

  const [status, setStatus] = useState(token ? "loading" : "error");
  const [message, setMessage] = useState(
    token ? "Verifying your email..." : "Verification token is missing."
  );

  const [email, setEmail] = useState("");
  const [resendLoading, setResendLoading] = useState(false);
  const [resendMessage, setResendMessage] = useState("");

  useEffect(() => {
    if (!token || hasVerifiedRef.current) return;
    hasVerifiedRef.current = true;

    async function verify() {
      try {
        const body = await apiFetch(
          `${API_BASE}/api/auth/verify-email?token=${token}`
        );

        setStatus("success");
        setMessage(body.data || body.message || "Email verified successfully.");
      } catch (err) {
        setStatus("error");
        setMessage(err.message || "Verification failed.");
      }
    }

    verify();
  }, [token]);

  async function handleResend() {
    if (!email) {
      setResendMessage("Please enter your email.");
      return;
    }

    try {
      setResendLoading(true);
      setResendMessage("");

      await apiFetch(`${API_BASE}/api/auth/resend-verification`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ email })
      });

      setResendMessage(
        "If the email exists and is not verified, a verification link has been sent."
      );
    } catch (err) {
      setResendMessage(
        "If the email exists and is not verified, a verification link has been sent."
      );
    } finally {
      setResendLoading(false);
    }
  }

  return (
    <div style={{ textAlign: "center", marginTop: "100px" }}>
      <h2>Email Verification</h2>

      {status === "loading" && <p>{message}</p>}

      {status === "success" && (
        <>
          <p>{message}</p>
          <Link to="/login">Go to Login</Link>
        </>
      )}

      {status === "error" && (
        <>
          <p>{message}</p>

          <div style={{ marginTop: "20px" }}>
            <h4>Resend Verification Email</h4>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                padding: "8px",
                marginRight: "10px",
                width: "220px"
              }}
            />

            <button
              onClick={handleResend}
              disabled={resendLoading}
              style={{ padding: "8px 12px" }}
            >
              {resendLoading ? "Sending..." : "Resend"}
            </button>

            {resendMessage && (
              <p style={{ marginTop: "10px" }}>{resendMessage}</p>
            )}
          </div>

          <div style={{ marginTop: "20px" }}>
            <Link to="/login">Back to Login</Link>
          </div>
        </>
      )}
    </div>
  );
}