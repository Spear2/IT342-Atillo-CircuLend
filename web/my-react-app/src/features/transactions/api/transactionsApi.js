import { getApiClient } from "@/shared/api/client/ApiClientSingleton";

export async function listAdminTransactions({ page = 0, size = 20 } = {}) {
  const res = await getApiClient().request(`/api/admin/transactions?page=${page}&size=${size}`, {
    method: "GET",
  });
  const body = await res.json();
  return body?.data || [];
}

export async function listAdminAuditLogs({ page = 0, size = 20 } = {}) {
  const res = await getApiClient().request(`/api/admin/logs?page=${page}&size=${size}`, {
    method: "GET",
  });
  const body = await res.json();
  return body?.data || [];
}
