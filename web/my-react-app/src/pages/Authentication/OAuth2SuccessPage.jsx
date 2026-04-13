import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { setRole, setToken } from "../../security/auth";

export default function OAuth2SuccessPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get("token");
    const role = (searchParams.get("role") || "BORROWER").toUpperCase();
    const oauthError = searchParams.get("error");

    if (oauthError) {
      navigate(`/login?oauth2=${encodeURIComponent(oauthError)}`, { replace: true });
      return;
    }

    if (!token) {
      navigate("/login?oauth2=missing_token", { replace: true });
      return;
    }

    setToken(token);
    setRole(role);

    navigate(role === "ADMIN" ? "/admin?login=google_success" : "/borrower?login=google_success", {replace: true,});
  }, [navigate, searchParams]);

  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
      <p>Signing you in with Google...</p>
    </div>
  );
}