import React, { useEffect, useState } from "react";
import { getApiClient } from "../../../api/ApiClientSingleton";
import transaction from "../../../assets/black-transaction.png"

const formatDate = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? "—" : d.toLocaleString();
};

const GlobalTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadTransactions() {
      setLoading(true);
      setError("");

      try {
        const res = await getApiClient().request("/api/admin/transactions", {
          method: "GET",
        });

        const body = await res.json();
        const rows = (body?.data || []).slice(0, 3); // remove .slice(...) if you want all

        if (!cancelled) setTransactions(rows);
      } catch (err) {
        if (!cancelled) {
          setTransactions([]);
          setError(err.message || "Failed to fetch transactions.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadTransactions();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <section className="admin-card">
      <div className="card-header">
        <img className="card-header-icon-img" src={transaction} alt="" />
        <span>Global Transactions</span>
      </div>

      <div className="table-responsive-wrapper">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Borrower</th>
              <th>Item Name</th>
              <th>Borrow Date</th>
              <th>Return Date</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {loading && (
              <tr>
                <td colSpan="5">Loading transactions...</td>
              </tr>
            )}

            {!loading && error && (
              <tr>
                <td colSpan="5">{error}</td>
              </tr>
            )}

            {!loading && !error && transactions.length === 0 && (
              <tr>
                <td colSpan="5">No transactions found.</td>
              </tr>
            )}

            {!loading &&
              !error &&
              transactions.map((tx) => (
                <tr key={tx.transactionId}>
                  <td>{tx.borrowerName || tx.borrowerEmail || "—"}</td>
                  <td>{tx.itemName}</td>
                  <td>{formatDate(tx.borrowDate)}</td>
                  <td>{formatDate(tx.returnDate)}</td>
                  <td>
                    <span className={`badge ${(tx.status || "").toLowerCase()}`}>
                      {tx.status}
                    </span>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>
    </section>
  );
};

export default GlobalTransactions;