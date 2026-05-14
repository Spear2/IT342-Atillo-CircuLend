import { getApiClient } from "@/shared/api/client/ApiClientSingleton";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export function getGoogleAuthUrl() {
  return `${API_BASE}/oauth2/authorization/google`;
}

export async function loginWithPassword({ email, password }) {
  return getApiClient().request("/api/auth/login", {
    method: "POST",
    auth: false,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: email?.trim(),
      password,
    }),
  });
}

export async function registerWithPassword({ firstName, lastName, email, password }) {
  return getApiClient().request("/api/auth/register", {
    method: "POST",
    auth: false,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      firstName: firstName?.trim(),
      lastName: lastName?.trim(),
      email: email?.trim().toLowerCase(),
      password,
    }),
  });
}
