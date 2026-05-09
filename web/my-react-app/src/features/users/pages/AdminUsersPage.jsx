import React, { useEffect, useState } from "react";
import AdminNavbar from "@/features/admin/components/AdminNavbar";
import AdminSidebar from "@/features/admin/components/AdminSidebar";
import { getApiClient } from "@/shared/api/client/ApiClientSingleton";
import "@/features/admin/pages/AdminDashboard.css";
import { useNavigate } from "react-router-dom"
import "@/features/transactions/pages/adminListPages.css";

const formatDate = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? "—" : d.toLocaleString();
};

export default function AdminUsersPage() {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const res = await getApiClient().request(`/api/admin/users?page=${page}&size=${size}`, {
          method: "GET",
        });
        const body = await res.json();
        const data = body?.data || [];
        if (!cancelled) setRows(data);
      } catch (err) {
        if (!cancelled) {
          setRows([]);
          setError(err.message || "Failed to fetch users.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => { cancelled = true; };
  }, [page, size]);

  const hasPrev = page > 0;
  const hasNext = rows.length === size; // simple heuristic

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-main-content">
        <AdminNavbar />
        <div className="admin-container">
          <header className="admin-header"><h1>Users</h1></header>

          <section className="admin-card">
            <div className="card-header"><span className="icon">👥</span> User Management</div>

            <div className="table-toolbar">
              <label>
                Rows:&nbsp;
                <select
                  value={size}
                  onChange={(e) => {
                    setSize(Number(e.target.value));
                    setPage(0);
                  }}
                >
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={50}>50</option>
                </select>
              </label>
            </div>

            <div className="table-responsive-wrapper">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>User ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Created At</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {loading && <tr><td colSpan="5">Loading users...</td></tr>}
                  {!loading && error && <tr><td colSpan="5">{error}</td></tr>}
                  {!loading && !error && rows.length === 0 && <tr><td colSpan="5">No users found.</td></tr>}
                  {!loading && !error && rows.map((u) => (
                    <tr key={u.userId}>
                      <td>{u.userId}</td>
                      <td>{`${u.firstname || ""} ${u.lastname || ""}`.trim() || "—"}</td>
                      <td>{u.email || "—"}</td>
                      <td>{u.role || "—"}</td>
                      <td>{formatDate(u.createdAt)}</td>
                      <td>
                        <button
                          type="button"
                          className="btn-small"
                          style={{ backgroundColor: "#5a67d8", color: "white" }}
                          onClick={() => navigate(`/admin/users/${u.userId}`)}
                        >
                          View
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="pagination-bar">
              <span>Page {page + 1}</span>
              <div className="pagination-actions">
                <button disabled={!hasPrev || loading} onClick={() => setPage((p) => Math.max(0, p - 1))}>
                  Previous
                </button>
                <button disabled={!hasNext || loading} onClick={() => setPage((p) => p + 1)}>
                  Next
                </button>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}