import { getApiClient } from "@/shared/api/client/ApiClientSingleton";

async function requestJson(path, options = {}) {
  const res = await getApiClient().request(path, options);
  let body = null;
  try {
    body = await res.json();
  } catch {
    body = null;
  }

  if (!res.ok || body?.success === false) {
    const message = body?.error?.message || body?.message || `Request failed (${res.status})`;
    const err = new Error(message);
    err.code = body?.error?.code;
    err.status = res.status;
    throw err;
  }
  return body;
}

export function listInventoryItems({ page = 0, size = 50 } = {}) {
  return requestJson(`/api/items?page=${page}&size=${size}`, { method: "GET" });
}

export function getInventoryItemById(itemId) {
  return requestJson(`/api/items/${itemId}`, { method: "GET" });
}

export function createInventoryItem(formData) {
  return requestJson("/api/admin/items", { method: "POST", body: formData });
}

export function updateInventoryItem(itemId, formData) {
  return requestJson(`/api/admin/items/${itemId}`, { method: "PUT", body: formData });
}

export function deleteInventoryItem(itemId) {
  return requestJson(`/api/admin/items/${itemId}`, { method: "DELETE" });
}
