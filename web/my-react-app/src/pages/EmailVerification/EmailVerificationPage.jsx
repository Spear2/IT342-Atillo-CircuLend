import React, { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";

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

    fetch(`${API_BASE}/api/auth/verify-email?token=${token}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          setStatus("success");
          setMessage(data.message || "Email verified successfully.");
        } else {
          setStatus("error");
          setMessage(data.message || "Verification failed.");
        }
      })
      .catch(() => {
        setStatus("error");
        setMessage("Could not connect to the server.");
      });
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