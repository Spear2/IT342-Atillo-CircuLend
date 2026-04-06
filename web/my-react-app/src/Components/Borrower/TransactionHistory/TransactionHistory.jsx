import React, { useMemo } from "react";
import "./transactionHistory.css";
import Camera from "../../../assets/camera.jpg"

export default function TransactionHistory({
  items = [],
  currentPage = 0,
  pageSize = 5,
  onPageChange,
}) {
  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));

  const pageItems = useMemo(() => {
    const start = currentPage * pageSize;
    return items.slice(start, start + pageSize);
  }, [items, currentPage, pageSize]);

  return (
    <div className="card no-padding">
      <div className="card-header">Transaction History</div>

      <table className="transaction-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Date Borrowed</th>
            <th>Date Returned</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          {pageItems.length === 0 ? (
            <tr>
              <td colSpan="4" className="empty-cell">No transaction history yet.</td>
            </tr>
          ) : (
            pageItems.map((tx) => (
              <tr key={tx.transactionId}>
                <td>
                  <div className="td-item">
                    <img
                      src={Camera}
                      alt={tx.itemName}
                    />
                    <div>
                      <p className="bold">{tx.itemName}</p>
                      <p className="item-category">📁 {tx.categoryName || "Uncategorized"}</p>
                    </div>
                  </div>
                </td>
                <td>{tx.borrowedLabel}</td>
                <td>{tx.returnedLabel}</td>
                <td>
                  <span
                    className={`status-badge ${
                      String(tx.status).toUpperCase() === "COMPLETED" ? "completed" : "active"
                    }`}
                  >
                    {String(tx.status).toUpperCase() === "COMPLETED" ? "Completed" : "Active"}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {items.length > pageSize && (
        <div className="pagination">
          <button
            className="page-btn nav-arrow"
            onClick={() => onPageChange(Math.max(0, currentPage - 1))}
            disabled={currentPage === 0}
            type="button"
          >
            &lt;
          </button>

          {Array.from({ length: totalPages }).map((_, idx) => (
            <button
              key={idx}
              className={`page-btn ${idx === currentPage ? "active" : ""}`}
              onClick={() => onPageChange(idx)}
              type="button"
            >
              {idx + 1}
            </button>
          ))}

          <button
            className="page-btn nav-arrow"
            onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
            disabled={currentPage >= totalPages - 1}
            type="button"
          >
            &gt;
          </button>
        </div>
      )}
    </div>
  );
}