import { getApiClient } from "@/shared/api/client/ApiClientSingleton";

export async function listAdminUsers({ page = 0, size = 20 } = {}) {
  const res = await getApiClient().request(`/api/admin/users?page=${page}&size=${size}`, {
    method: "GET",
  });
  const body = await res.json();
  return body?.data || [];
}
