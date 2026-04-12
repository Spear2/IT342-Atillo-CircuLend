import { setRole, setToken } from "../security/auth";
import { getApiClient } from "../api/ApiClientSingleton";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

// Backend endpoint that accepts Google token and returns your JWT
const BACKEND_GOOGLE_ENDPOINT = "/api/auth/google";

// Lazy-load GIS script
function loadGoogleScript() {
  return new Promise((resolve, reject) => {
    if (window.google?.accounts?.oauth2) return resolve();

    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client";
    script.async = true;
    script.defer = true;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error("Failed to load Google SDK"));
    document.head.appendChild(script);
  });
}

// Popup flow (button click only)
async function getGoogleAccessTokenByPopup() {
  await loadGoogleScript();

  return new Promise((resolve, reject) => {
    if (!window.google?.accounts?.oauth2) {
      reject(new Error("Google OAuth client is unavailable."));
      return;
    }

    const tokenClient = window.google.accounts.oauth2.initTokenClient({
      client_id: GOOGLE_CLIENT_ID,
      scope: "openid email profile",
      prompt: "consent",
      callback: async (resp) => {
        if (resp?.error) {
          reject(new Error(resp.error_description || resp.error || "Google OAuth failed"));
          return;
        }
        if (!resp?.access_token) {
          reject(new Error("No access token received from Google."));
          return;
        }
        resolve(resp.access_token);
      },
    });

    tokenClient.requestAccessToken();
  });
}

// Fetch user info from Google using access token
async function fetchGoogleUserInfo(accessToken) {
  const res = await fetch("https://openidconnect.googleapis.com/v1/userinfo", {
    headers: { Authorization: `Bearer ${accessToken}` },
  });

  if (!res.ok) {
    throw new Error("Failed to fetch Google user info.");
  }

  return res.json(); // contains sub, email, given_name, family_name, etc.
}

/**
 * Button-driven Google sign-in (no One Tap prompt).
 * Backend expects token string under idToken field for now.
 * If backend expects a different field, adjust payload below.
 */
export async function signInWithGooglePopup({ navigate }) {
  if (!GOOGLE_CLIENT_ID) {
    throw new Error("Missing VITE_GOOGLE_CLIENT_ID in frontend env.");
  }

  const accessToken = await getGoogleAccessTokenByPopup();
  const profile = await fetchGoogleUserInfo(accessToken);

  // Send token/profile to backend. You can choose one:
  // A) send access token and verify with Google userinfo in backend
  // B) send profile + token
  const api = getApiClient();
  const body = await api.post(
    BACKEND_GOOGLE_ENDPOINT,
    {
      // Keep key as idToken only if your backend currently expects that exact key.
      // Better name is googleAccessToken, but keep backend/frontend consistent.
      idToken: accessToken,
      profile, // optional helper payload
    },
    { auth: false }
  );

  const jwt = body?.data?.accessToken;
  const role = body?.data?.user?.role || "BORROWER";

  if (!jwt) throw new Error("Backend did not return JWT.");

  setToken(jwt);
  setRole(role);

  if (role.toUpperCase() === "ADMIN") navigate("/admin", { replace: true });
  else navigate("/borrower", { replace: true });

  return body;
}