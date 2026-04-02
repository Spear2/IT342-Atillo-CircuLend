import React, { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { apiFetch } from "../../Utils/apiFetch";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const [status, setStatus] = useState(token ? "loading" : "error");
  const [message, setMessage] = useState(
    token ? "Verifying your email..." : "Verification token is missing."
  );

  useEffect(() => {
    if (!token) return;

    async function verify() {
        try {
        const body = await apiFetch(
            `${API_BASE}/api/auth/verify-email?token=${token}`
        );

        setStatus("success");
        setMessage(body.data || "Email verified successfully.");

        } catch (err) {
        setStatus("error");
        setMessage(err.message);
        }
    }

    verify();
    }, [token]);

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
          <Link to="/login">Back to Login</Link>
        </>
      )}
    </div>
  );
}