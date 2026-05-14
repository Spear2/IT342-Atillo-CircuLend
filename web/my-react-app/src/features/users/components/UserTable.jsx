import React, { useEffect, useState } from "react";
import { getApiClient } from "@/shared/api/client/ApiClientSingleton";
import usersIcon from "@/assets/user.png"

const UserManagementTable = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadUsers() {
      setLoading(true);
      setError("");

      try {
        const res = await getApiClient().request("/api/admin/users?page=0&size=3", {
          method: "GET",
        });

        const body = await res.json();
        const rows = body?.data || [];

        if (!cancelled) setUsers(rows);
      } catch (err) {
        if (!cancelled) {
          setUsers([]);
          setError(err.message || "Failed to fetch users.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadUsers();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <section className="admin-card">
      <div className="card-header">
        <img className="card-header-icon-img" src={usersIcon} alt="" /> User Management
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {loading && (
            <tr>
              <td colSpan="4">Loading users...</td>
            </tr>
          )}

          {!loading && error && (
            <tr>
              <td colSpan="4">{error}</td>
            </tr>
          )}

          {!loading && !error && users.length === 0 && (
            <tr>
              <td colSpan="4">No users found.</td>
            </tr>
          )}

          {!loading &&
            !error &&
            users.map((user) => {
              const fullName = `${user.firstname || ""} ${user.lastname || ""}`.trim();
              return (
                <tr key={user.userId}>
                  <td>{fullName || "—"}</td>
                  <td>{user.email || "—"}</td>
                  <td>{user.role || "—"}</td>
                  <td>
                    <button className="btn-small" style={{backgroundColor: "#5a67d8", color: "white"}}>View</button>
                  </td>
                </tr>
              );
            })}
        </tbody>
      </table>
    </section>
  );
};

export default UserManagementTable;