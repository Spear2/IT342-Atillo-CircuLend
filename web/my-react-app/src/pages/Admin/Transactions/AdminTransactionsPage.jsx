import React, { useEffect, useState } from "react";
import AdminNavbar from "../../../Components/Admin/Navbar/Navbar";
import AdminSidebar from "../../../Components/Admin/Sidebar/Sidebar";
import { getApiClient } from "../../../api/ApiClientSingleton";
import "../Dashboard/adminDashboard.css";
import "./adminListPages.css";

const formatDate = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? "—" : d.toLocaleString();
};

export default function AdminTransactionsPage() {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const res = await getApiClient().request(`/api/admin/transactions?page=${page}&size=${size}`, {
          method: "GET",
        });
        const body = await res.json();
        if (!cancelled) setRows(body?.data || []);
      } catch (err) {
        if (!cancelled) {
          setRows([]);
          setError(err.message || "Failed to fetch transactions.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => { cancelled = true; };
  }, [page, size]);

  const hasPrev = page > 0;
  const hasNext = rows.length === size;

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-main-content">
        <AdminNavbar />
        <div className="admin-container">
          <header className="admin-header"><h1>Global Transactions</h1></header>

          <section className="admin-card">
            <div className="card-header"><span className="icon">⏳</span> All Transactions</div>

            <div className="table-toolbar">
              <label>
                Rows:&nbsp;
                <select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(0); }}>
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
                    <th>ID</th>
                    <th>Borrower</th>
                    <th>Item</th>
                    <th>Asset Tag</th>
                    <th>Borrow Date</th>
                    <th>Return Date</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {loading && <tr><td colSpan="7">Loading transactions...</td></tr>}
                  {!loading && error && <tr><td colSpan="7">{error}</td></tr>}
                  {!loading && !error && rows.length === 0 && <tr><td colSpan="7">No transactions found.</td></tr>}
                  {!loading && !error && rows.map((tx) => (
                    <tr key={tx.transactionId}>
                      <td>{tx.transactionId}</td>
                      <td>{tx.borrowerName || tx.borrowerEmail || "—"}</td>
                      <td>{tx.itemName || "—"}</td>
                      <td>{tx.assetTag || "—"}</td>
                      <td>{formatDate(tx.borrowDate)}</td>
                      <td>{formatDate(tx.returnDate)}</td>
                      <td><span className={`badge ${(tx.status || "").toLowerCase()}`}>{tx.status || "—"}</span></td>
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