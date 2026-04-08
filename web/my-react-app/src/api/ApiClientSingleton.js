import { getAuthHeader } from "../security/auth";

let clientInstance = null;

function normalizeBaseUrl() {
  const base = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
  return base.replace(/\/$/, "");
}

function resolveUrl(path) {
  if (path.startsWith("http://") || path.startsWith("https://")) {
    return path;
  }
  const p = path.startsWith("/") ? path : `/${path}`;
  return `${normalizeBaseUrl()}${p}`;
}

/** Singleton: one shared client for CircuLend backend base URL and requests. */
export function getApiClient() {
  if (!clientInstance) {
    clientInstance = {
      getBaseUrl: () => normalizeBaseUrl(),

      /**
       * @param {string} path
       * @param {RequestInit & { auth?: boolean }} options — auth: false for login/register
       */
      request(path, options = {}) {
        const { auth = true, headers: incoming = {}, ...rest } = options;
        const headers = { ...incoming };
        if (auth !== false) {
            Object.assign(headers, getAuthHeader());
        }
        return fetch(resolveUrl(path), { ...rest, headers });
        }
    };
  }
  return clientInstance;
}