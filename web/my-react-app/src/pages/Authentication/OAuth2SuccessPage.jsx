import React, { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { setRole, setToken } from "../../security/auth";

export default function OAuth2SuccessPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get("token");
    const role = (searchParams.get("role") || "BORROWER").toUpperCase();

    if (!token) {
      navigate("/login?oauth2=missing_token", { replace: true });
      return;
    }

    setToken(token);
    setRole(role);

    if (role === "ADMIN") {
      navigate("/admin", { replace: true });
    } else {
      navigate("/borrower", { replace: true });
    }
  }, [navigate, searchParams]);

  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
      <p>Signing you in with Google...</p>
    </div>
  );
}