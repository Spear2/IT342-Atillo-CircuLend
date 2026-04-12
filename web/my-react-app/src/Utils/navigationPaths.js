import { getRole } from "../security/auth";

/** Where “home” is after login — borrower dashboard vs admin. */
export function getDashboardPathForUser() {
  const role = (getRole() || "").toUpperCase();
  if (role === "ADMIN") return "/admin/";
  return "/borrower/dashboard";
}