export async function apiFetch(url, options = {}) {
  const res = await fetch(url, options);

  let body = null;
  try {
    body = await res.json();
  } catch {
    body = null;
  }

  const ok = res.ok && body?.success !== false;

  if (!ok) {
    const code = body?.error?.code || "SYSTEM-001";
    const message =
      body?.error?.message ||
      body?.message ||
      `Request failed (${res.status})`;

    const err = new Error(message);
    err.code = code;
    err.status = res.status;
    err.details = body?.error?.details ?? null;
    throw err;
  }

  return body;
}