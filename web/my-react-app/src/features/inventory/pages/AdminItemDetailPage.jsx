import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AdminNavbar from "@/features/admin/components/AdminNavbar";
import Footer from "@/shared/layout/Footer";
import { getApiClient } from "@/shared/api/client/ApiClientSingleton";
import "./adminItemDetailPage.css";
import electronics from "@/assets/electronics.png"
import exercise from "@/assets/exercise.png"
import sports from "@/assets/sports.png"
import technology from "@/assets/technology.png"
import instruments from "@/assets/instruments.png"

const CATEGORY_ICONS = {
  electronics,
  exercise,
  sports,
  technology,
  instruments,
};

const initialForm = {
  name: "",
  description: "",
  assetTag: "",
  categoryId: "",
  imageFile: null,
};

async function requestJson(path, options = {}) {
  const res = await getApiClient().request(path, options);

  let body = null;
  try {
    body = await res.json();
  } catch {
    body = null;
  }

  if (!res.ok || body?.success === false) {
    const message = body?.error?.message || body?.message || `Request failed (${res.status})`;
    const err = new Error(message);
    err.code = body?.error?.code;
    err.status = res.status;
    throw err;
  }

  return body;
}

function formatDate(v) {
  if (!v) return "—";
  const d = new Date(v);
  return Number.isNaN(d.getTime()) ? "—" : d.toLocaleDateString();
}


export default function AdminItemDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [item, setItem] = useState(null);
  const [similarItems, setSimilarItems] = useState([]);
  const [itemTx, setItemTx] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(initialForm);
  const [submitting, setSubmitting] = useState(false);

  const currentHolder = useMemo(() => {
    const activeTx = itemTx.find((tx) => normalizeTxStatus(tx.status) === "ACTIVE");
    if (!activeTx) return null;
    return activeTx.borrowerName || activeTx.borrowerEmail || `User ID: ${activeTx.userId ?? "—"}`;
  }, [itemTx]);

  const last3 = useMemo(() => {
    return itemTx
      .filter((tx) => normalizeTxStatus(tx.status) === "COMPLETED")
      .slice(0, 3);
  }, [itemTx]);

  const loadDetail = async () => {
    setLoading(true);
    setError("");
    try {
      const itemBody = await requestJson(`/api/items/${id}`, { method: "GET" });
      const detail = itemBody?.data;
      setItem(detail);

      const listBody = await requestJson("/api/items?page=0&size=20", { method: "GET" });
      const content = listBody?.data?.content || [];
      const sameCategory = content.filter(
        (x) => x.itemId !== detail.itemId && x.categoryId === detail.categoryId
      );
      setSimilarItems(sameCategory.slice(0, 3));

      const txBody = await requestJson("/api/admin/transactions?page=0&size=200", { method: "GET" });
      const txAll = txBody?.data || [];
      const filtered = txAll
        .filter((tx) => Number(tx.itemId) === Number(detail.itemId))
        .sort((a, b) => new Date(b.borrowDate || 0) - new Date(a.borrowDate || 0));

      setItemTx(filtered);
    } catch (err) {
      setError(err.message || "Failed to load item details.");
      setItem(null);
      setSimilarItems([]);
      setItemTx([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDetail();
  }, [id]);

  const openEditModal = () => {
    if (!item) return;
    setForm({
      name: item.name || "",
      description: item.description || "",
      assetTag: item.assetTag || "",
      categoryId: item.categoryId ? String(item.categoryId) : "",
      imageFile: null,
    });
    setActionError("");
    setModalOpen(true);
  };

  const closeModal = () => {
    if (submitting) return;
    setModalOpen(false);
    setForm(initialForm);
    setActionError("");
  };

  const onFormChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "imageFile") {
      setForm((prev) => ({ ...prev, imageFile: files?.[0] || null }));
      return;
    }
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setActionError("");

    try {
      if (!form.name.trim() || !form.assetTag.trim() || !String(form.categoryId).trim()) {
        throw new Error("Name, asset tag, and category are required.");
      }

      const fd = new FormData();
      fd.append("name", form.name.trim());
      fd.append("description", form.description || "");
      fd.append("assetTag", form.assetTag.trim());
      fd.append("categoryId", String(form.categoryId).trim());
      if (form.imageFile) fd.append("imageFile", form.imageFile);

      await requestJson(`/api/admin/items/${id}`, { method: "PUT", body: fd });
      closeModal();
      await loadDetail();
    } catch (err) {
      setActionError(err.message || "Failed to update item.");
    } finally {
      setSubmitting(false);
    }
  };

  function normalizeTxStatus(status) {
    const s = String(status || "").toUpperCase();
    if (s === "ACTIVE" || s === "BORROWED") return "ACTIVE";
    if (s === "COMPLETED" || s === "RETURNED") return "COMPLETED";
    return s;
  }

  function displayTxStatus(status) {
    const s = normalizeTxStatus(status);
    if (s === "ACTIVE") return "Borrowed";
    if (s === "COMPLETED") return "Completed";
    return s || "—";
  }

  const handleDelete = async () => {
    if (!item) return;
    const ok = window.confirm(`Delete "${item.name}"?`);
    if (!ok) return;

    try {
      await requestJson(`/api/admin/items/${id}`, { method: "DELETE" });
      navigate("/admin/inventory", { replace: true });
    } catch (err) {
      setActionError(err.message || "Failed to delete item.");
    }
  };
  function normalizeCategoryKey(categoryName) {
    // handles "Electronics", "electornics", "electronic items", etc.
    const raw = String(categoryName || "").toLowerCase().trim();
    if (raw.includes("electornic")) return "electronics"; // typo-safe
    if (raw.includes("electronic")) return "electronics";
    if (raw.includes("exercise") || raw.includes("fitness") || raw.includes("gym")) return "exercise";
    if (raw.includes("sport")) return "sports";
    if (raw.includes("instrument")) return "instruments";
    if (raw.includes("tech") || raw.includes("gadget")) return "technology";
    return "technology"; // fallback
  }
  function getCategoryIcon(categoryName) {
    const key = normalizeCategoryKey(categoryName);
    return CATEGORY_ICONS[key] || technology;
  }

  if (loading) return <div className="admin-item-page"><AdminNavbar /><main className="admin-item-main"><div className="alert">Loading item details...</div></main><Footer /></div>;
  if (error || !item) return <div className="admin-item-page"><AdminNavbar /><main className="admin-item-main"><div className="alert error">{error || "Item not found."}</div></main><Footer /></div>;

  return (
    <div className="admin-item-page">
      <AdminNavbar />

      <main className="admin-item-main">
        <h1 className="admin-item-title">Item Details</h1>

        <section className="admin-item-top">
          <div className="admin-item-image-wrap">
            <img
              src={item.imageFileUrl || "https://via.placeholder.com/900x560"}
              alt={item.name}
              className="admin-item-image"
            />
          </div>

          <aside className="admin-item-panel">
            <h2>{item.name}</h2>

            <p className="admin-item-category">
              <img
                src={getCategoryIcon(item.categoryName)}
                alt={item.categoryName || "Category"}
                className="inline-icon"
              />
              <span>{item.categoryName || "Uncategorized"}</span>
            </p>

            <div className={`detail-status ${String(item.status || "").toLowerCase()}`}>
              
              <span>● {item.status || "—"}</span>
            </div>

            <hr />

            <h3>Description</h3>
            <p className="admin-item-description">{item.description || "No description available."}</p>

            <div className="admin-details-box">
              <h4>Admin Details</h4>
              <p><strong>Current Holder:</strong> {currentHolder || "None"}</p>

              <h5>Last 3 transactions</h5>
              {last3.length === 0 ? (
                <p className="muted">No transactions yet.</p>
              ) : (
                <ul className="tx-list">
                  {last3.map((tx) => (
                    <li key={tx.transactionId}>
                      {formatDate(tx.borrowDate)}: {displayTxStatus(tx.status)} by {tx.borrowerName || tx.borrowerEmail || `User #${tx.userId}`}
                    </li>
                  ))}
                </ul>
              )}
            </div>

            {actionError && <div className="alert error">{actionError}</div>}

            <div className="admin-item-actions">
              <button type="button" className="edit-btn" onClick={openEditModal}>Edit Item</button>
              <button type="button" className="delete-btn" onClick={handleDelete}>Delete Item</button>
            </div>
          </aside>
        </section>

        <section className="admin-similar-section">
          <div className="admin-similar-header">
            <h3>Similar Items</h3>
            <button type="button" className="see-all-btn" onClick={() => navigate("/admin/inventory")}>
              See All
            </button>
          </div>

          <div className="admin-similar-grid">
            {similarItems.length > 0 ? (
              similarItems.map((s) => (
                <article key={s.itemId} className="admin-similar-card">
                  <img src={s.imageFileUrl || "https://via.placeholder.com/320x200"} alt={s.name} />
                  <div className="admin-similar-body">
                    <h4>{s.name}</h4>
                    <p>{s.categoryName || "Uncategorized"}</p>
                    <button type="button" onClick={() => navigate(`/admin/items/${s.itemId}`)}>View Details</button>
                  </div>
                </article>
              ))
            ) : (
              <div className="alert">No similar items found.</div>
            )}
          </div>
        </section>
      </main>

      <Footer />

      {modalOpen && (
        <div className="inv-modal-backdrop">
          <div className="inv-modal">
            <h2>Edit Item</h2>
            <form onSubmit={handleEditSubmit} className="inv-form">
              <label>Name<input name="name" value={form.name} onChange={onFormChange} required /></label>
              <label>Description<textarea name="description" value={form.description} onChange={onFormChange} rows={3} /></label>
              <label>Asset Tag<input name="assetTag" value={form.assetTag} onChange={onFormChange} required /></label>
              <label>Category ID<input name="categoryId" type="number" value={form.categoryId} onChange={onFormChange} required /></label>
              <label>Replace Image (optional)<input name="imageFile" type="file" accept="image/png,image/jpeg,image/webp" onChange={onFormChange} /></label>

              {actionError && <div className="alert error">{actionError}</div>}

              <div className="inv-modal-actions">
                <button type="button" onClick={closeModal} disabled={submitting} style={{color:"white"}}>Cancel</button>
                <button type="submit" className="btn-add-item" disabled={submitting}>
                  {submitting ? "Saving..." : "Update Item"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}