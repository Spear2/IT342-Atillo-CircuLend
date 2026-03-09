const TOKEN_KEY = "token";
const ROLE_KEY = "role";

const normalizeToken = (value) => {
  if (!value) return "";
  return value.replace(/^Bearer\s*/i, "").trim();
};

export function setToken(token) {
  const normalized = normalizeToken(token);
  if (normalized) localStorage.setItem(TOKEN_KEY, normalized);
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setRole(role) {
  if (role) localStorage.setItem(ROLE_KEY, role);
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY);
}

export function logout() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(ROLE_KEY);
}

export function getAuthHeader() {
  const token = normalizeToken(getToken());
  return token ? { Authorization: `Bearer ${token}` } : {};
}