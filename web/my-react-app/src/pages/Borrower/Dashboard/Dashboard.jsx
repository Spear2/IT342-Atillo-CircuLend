import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";
import BorrowerNavbar from "../../../Components/Borrower/BorrowerNavbar/Navbar";
import ActiveBorrowedItems from "../../../Components/Borrower/ActiveBorrowedItems/ActiveBorrowedItems";
import TransactionHistory from "../../../Components/Borrower/TransactionHistory/TransactionHistory";
import Footer from "../../../Components/Shared/Footer/Footer";
import { apiFetch } from "../../../Utils/apiFetch";
import { getAuthHeader } from "../../../security/auth";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const formatDate = (value) => {
  if (!value) return "-";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "-";
  return d.toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" });
};

export default function BorrowerDashboard() {
  const navigate = useNavigate();

  const [transactions, setTransactions] = useState([]);
  const [itemMeta, setItemMeta] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [returningTxId, setReturningTxId] = useState(null);
  const [historyPage, setHistoryPage] = useState(0);
  const pageSize = 5;

  // Return modal states
  const [showReturnModal, setShowReturnModal] = useState(false);
  const [selectedTx, setSelectedTx] = useState(null);
  const [returnAssetTag, setReturnAssetTag] = useState("");
  const [returnError, setReturnError] = useState("");

  const loadTransactions = async () => {
    setLoading(true);
    setError("");
    try {
      const body = await apiFetch(`${API_BASE}/api/transactions/user`, {
        method: "GET",
        headers: { ...getAuthHeader() },
      });

      const tx = Array.isArray(body?.data) ? body.data : [];
      setTransactions(tx);

      const uniqueItemIds = [...new Set(tx.map((t) => t.itemId).filter(Boolean))];
      const detailPairs = await Promise.all(
        uniqueItemIds.map(async (itemId) => {
          try {
            const itemRes = await apiFetch(`${API_BASE}/api/items/${itemId}`, {
              method: "GET",
              headers: { ...getAuthHeader() },
            });
            return [itemId, itemRes?.data || null];
          } catch {
            return [itemId, null];
          }
        })
      );

      setItemMeta(Object.fromEntries(detailPairs));
    } catch (err) {
      if (err.code === "AUTH-002") {
        setError("Session expired. Please log in again.");
        navigate("/login", { replace: true });
      } else {
        setError(err.message || "Failed to load dashboard data.");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTransactions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const activeItems = useMemo(
    () =>
      transactions
        .filter((t) => String(t.status).toUpperCase() === "ACTIVE")
        .map((t) => ({
          ...t,
          imageFileUrl: itemMeta[t.itemId]?.imageFileUrl || "",
          categoryName: itemMeta[t.itemId]?.categoryName || "",
          borrowedLabel: formatDate(t.borrowDate),
        })),
    [transactions, itemMeta]
  );

  const historyItems = useMemo(
    () =>
      transactions.map((t) => ({
        ...t,
        imageFileUrl: itemMeta[t.itemId]?.imageFileUrl || "",
        categoryName: itemMeta[t.itemId]?.categoryName || "",
        borrowedLabel: formatDate(t.borrowDate),
        returnedLabel: formatDate(t.returnDate),
      })),
    [transactions, itemMeta]
  );

  // Open modal instead of directly returning
  const openReturnModal = (tx) => {
    setSelectedTx(tx);
    setReturnAssetTag("");
    setReturnError("");
    setShowReturnModal(true);
  };

  const closeReturnModal = () => {
    if (returningTxId) return;
    setShowReturnModal(false);
    setSelectedTx(null);
    setReturnAssetTag("");
    setReturnError("");
  };

  const confirmReturn = async () => {
    if (!selectedTx) return;
    if (!returnAssetTag.trim()) {
      setReturnError("Please enter the item asset tag.");
      return;
    }

    setReturningTxId(selectedTx.transactionId);
    setReturnError("");

    try {
      await apiFetch(`${API_BASE}/api/transactions/return/${selectedTx.transactionId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...getAuthHeader(),
        },
        body: JSON.stringify({
          assetTag: returnAssetTag.trim(),
        }),
      });

      closeReturnModal();
      await loadTransactions();
    } catch (err) {
      if (err.code === "VALID-001") {
        setReturnError("Asset tag mismatch. Please check the physical tag and try again.");
      } else if (err.code === "BUSINESS-002") {
        setReturnError("You cannot return an item you did not borrow.");
      } else if (err.code === "BUSINESS-001") {
        setReturnError("This transaction is not active anymore.");
      } else {
        setReturnError(err.message || "Return failed.");
      }
    } finally {
      setReturningTxId(null);
    }
  };

  return (
    <div className="dashboard-wrapper">
      <BorrowerNavbar />

      <main className="dashboard-container">
        <h1 className="dashboard-title">Dashboard</h1>

        {loading && <div className="dashboard-alert">Loading dashboard...</div>}
        {error && <div className="dashboard-alert dashboard-alert-error">{error}</div>}

        {!loading && !error && (
          <>
            <section className="dashboard-section">
              <h2 className="section-title">Active Borrowed Items</h2>
              <ActiveBorrowedItems
                items={activeItems}
                returningTxId={returningTxId}
                onReturn={openReturnModal}
              />
            </section>

            <section className="dashboard-section">
              <h2 className="section-title">Transaction History</h2>
              <TransactionHistory
                items={historyItems}
                currentPage={historyPage}
                pageSize={pageSize}
                onPageChange={setHistoryPage}
              />
            </section>
          </>
        )}
      </main>

      <Footer />

      {showReturnModal && selectedTx && (
        <div className="return-modal-overlay" onClick={closeReturnModal}>
          <div className="return-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Confirm Return</h3>
            <p>
              <strong>Item:</strong> {selectedTx.itemName}
            </p>
            <p>
              <strong>Borrowed:</strong> {formatDate(selectedTx.borrowDate)}
            </p>

            <label htmlFor="returnAssetTag">Asset Tag</label>
            <input
              id="returnAssetTag"
              type="text"
              value={returnAssetTag}
              onChange={(e) => setReturnAssetTag(e.target.value)}
              placeholder="Enter physical asset tag (e.g. DSLR-001)"
              disabled={Boolean(returningTxId)}
            />

            {returnError && <div className="dashboard-alert dashboard-alert-error">{returnError}</div>}

            <div className="return-modal-actions">
              <button
                type="button"
                className="btn-cancel"
                onClick={closeReturnModal}
                disabled={Boolean(returningTxId)}
              >
                Cancel
              </button>
              <button
                type="button"
                className="btn-confirm"
                onClick={confirmReturn}
                disabled={Boolean(returningTxId)}
              >
                {returningTxId ? "Returning..." : "Confirm Return"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}