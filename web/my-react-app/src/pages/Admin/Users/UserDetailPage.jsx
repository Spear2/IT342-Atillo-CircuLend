import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AdminNavbar from "../../../Components/Admin/Navbar/Navbar";
import AdminSidebar from "../../../Components/Admin/Sidebar/Sidebar";
import { getApiClient } from "../../../api/ApiClientSingleton";
import "../Dashboard/adminDashboard.css";
import "../Transactions/adminListPages.css";
import users from "../../../assets/user.png"
import borrowing from "../../../assets/borrowing.png"
import transaction from "../../../assets/black-transaction.png"

const ICONS = {
  users: users,
  borrowing: borrowing,
  transaction: transaction,
  
};

const formatDate = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? "—" : d.toLocaleString();
};

const normalizeTxStatus = (status) => {
  const s = String(status || "").toUpperCase();
  if (s === "ACTIVE" || s === "BORROWED") return "ACTIVE";
  if (s === "COMPLETED" || s === "RETURNED") return "COMPLETED";
  return s || "—";
};

export default function UserDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [txRows, setTxRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");

      try {
        // You currently don't have /api/admin/users/{id}, so fetch a page and find by id.
        const usersRes = await getApiClient().request("/api/admin/users?page=0&size=200", {
          method: "GET",
        });
        const usersBody = await usersRes.json();
        const users = usersBody?.data || [];
        const found = users.find((u) => Number(u.userId) === Number(id));

        if (!found) {
          throw new Error("User not found.");
        }

        const txRes = await getApiClient().request("/api/admin/transactions?page=0&size=500", {
          method: "GET",
        });
        const txBody = await txRes.json();
        const allTx = txBody?.data || [];
        const userTx = allTx
          .filter((tx) => Number(tx.userId) === Number(id))
          .sort((a, b) => new Date(b.borrowDate || 0) - new Date(a.borrowDate || 0));

        if (!cancelled) {
          setUser(found);
          setTxRows(userTx);
        }
      } catch (err) {
        if (!cancelled) {
          setUser(null);
          setTxRows([]);
          setError(err.message || "Failed to load user details.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [id]);

  const stats = useMemo(() => {
    const active = txRows.filter((t) => normalizeTxStatus(t.status) === "ACTIVE").length;
    const completed = txRows.filter((t) => normalizeTxStatus(t.status) === "COMPLETED").length;
    return {
      total: txRows.length,
      active,
      completed,
    };
  }, [txRows]);

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-main-content">
        <AdminNavbar />

        <div className="admin-container">
          <header className="admin-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <h1>User Detail</h1>
            <button type="button" className="btn-add-item" onClick={() => navigate("/admin/users")}>
              Back to Users
            </button>
          </header>

          {loading && <div className="alert">Loading user details...</div>}
          {!loading && error && <div className="alert-error">{error}</div>}

          {!loading && !error && user && (
            <>
              <section className="admin-card" style={{ marginBottom: "1rem" }}>
                <div className="card-header">
                    <img className="card-header-icon-img" src={ICONS.users} alt="" />
                    <span>User Profile</span> 
                </div>

                <div style={{ padding: "1rem", display: "grid", gridTemplateColumns: "repeat(2, minmax(0, 1fr))", gap: "0.75rem" }}>
                  <div><strong>User ID:</strong> {user.userId}</div>
                  <div><strong>Role:</strong> {user.role || "—"}</div>
                  <div><strong>Name:</strong> {`${user.firstname || ""} ${user.lastname || ""}`.trim() || "—"}</div>
                  <div><strong>Created At:</strong> {formatDate(user.createdAt)}</div>
                  <div style={{ gridColumn: "1 / -1" }}><strong>Email:</strong> {user.email || "—"}</div>
                </div>
              </section>

              <section className="admin-card" style={{ marginBottom: "1rem" }}>
                <div className="card-header">
                    <img className="card-header-icon-img" src={ICONS.borrowing} alt="" />
                    <span>Borrowing Summary</span> 
                </div>

                <div style={{ padding: "1rem", display: "flex", gap: "1rem", flexWrap: "wrap" }}>
                  <div><strong>Total Transactions:</strong> {stats.total}</div>
                  <div><strong>Active:</strong> {stats.active}</div>
                  <div><strong>Completed:</strong> {stats.completed}</div>
                </div>
              </section>

              <section className="admin-card">
                <div className="card-header">
                    <img className="card-header-icon-img" src={ICONS.transaction} alt="" />
                  <span>Transaction History</span> 
                </div>

                <div className="table-responsive-wrapper">
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>Transaction ID</th>
                        <th>Item</th>
                        <th>Asset Tag</th>
                        <th>Borrow Date</th>
                        <th>Return Date</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {txRows.length === 0 ? (
                        <tr>
                          <td colSpan="6">No transactions found for this user.</td>
                        </tr>
                      ) : (
                        txRows.map((tx) => (
                          <tr key={tx.transactionId}>
                            <td>{tx.transactionId}</td>
                            <td>{tx.itemName || "—"}</td>
                            <td>{tx.assetTag || "—"}</td>
                            <td>{formatDate(tx.borrowDate)}</td>
                            <td>{formatDate(tx.returnDate)}</td>
                            <td>{normalizeTxStatus(tx.status)}</td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </section>
            </>
          )}
        </div>
      </div>
    </div>
  );
}