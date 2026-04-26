import React, { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import AdminNavbar from "../../../Components/Admin/Navbar/Navbar";
import AdminSidebar from "../../../Components/Admin/Sidebar/Sidebar";
import { getApiClient } from "../../../api/ApiClientSingleton";
import "../Dashboard/adminDashboard.css";
import "./inventoryManagementPage.css";

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
    const message =
      body?.error?.message || body?.message || `Request failed (${res.status})`;
    const err = new Error(message);
    err.code = body?.error?.code;
    err.status = res.status;
    throw err;
  }

  return body;
}

export default function InventoryManagementPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [successMessage, setSuccessMessage] = useState("");
  const [actionError, setActionError] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [mode, setMode] = useState("create"); // create | edit
  const [selectedItem, setSelectedItem] = useState(null);

  const [form, setForm] = useState(initialForm);
  const [submitting, setSubmitting] = useState(false);

  const hasCategories = useMemo(() => categories.length > 0, [categories]);

  const loadItems = async () => {
    setLoading(true);
    setError("");
    try {
      const body = await requestJson("/api/items?page=0&size=50", { method: "GET" });
      setItems(body?.data?.content || []);
    } catch (err) {
      setItems([]);
      setError(err.message || "Failed to load inventory items.");
    } finally {
      setLoading(false);
    }
  };

  const loadCategories = async () => {
    // Optional endpoint; if not available, we fallback to categoryId text input
    try {
      const body = await requestJson("/api/categories", { method: "GET" });
      setCategories(body?.data || []);
    } catch {
      setCategories([]);
    }
  };

  useEffect(() => {
    loadItems();
    loadCategories();
  }, []);

  useEffect(() => {
    if (searchParams.get("new") === "1") {
      openCreateModal();
      searchParams.delete("new");
      setSearchParams(searchParams, { replace: true });
    }
  }, [searchParams, setSearchParams]);

  const openCreateModal = () => {
    setMode("create");
    setSelectedItem(null);
    setForm(initialForm);
    setActionError("");
    setModalOpen(true);
  };

  const openEditModal = (item) => {
    setMode("edit");
    setSelectedItem(item);
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
    setSelectedItem(null);
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

  const buildFormData = () => {
    const fd = new FormData();

    fd.append("name", form.name.trim());
    fd.append("description", form.description);
    fd.append("assetTag", form.assetTag.trim());
    fd.append("categoryId", String(form.categoryId).trim());

    if (form.imageFile) {
      fd.append("imageFile", form.imageFile);
    }
    return fd;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setActionError("");
    setSuccessMessage("");

    try {
      if (!form.name.trim() || !form.assetTag.trim() || !String(form.categoryId).trim()) {
        throw new Error("Name, asset tag, and category are required.");
      }

      if (mode === "create" && !form.imageFile) {
        throw new Error("Image is required when creating an item.");
      }

      const fd = buildFormData();

      if (mode === "create") {
        await requestJson("/api/admin/items", {
          method: "POST",
          body: fd,
        });
        setSuccessMessage("Item created successfully.");
      } else {
        await requestJson(`/api/admin/items/${selectedItem.itemId}`, {
          method: "PUT",
          body: fd,
        });
        setSuccessMessage("Item updated successfully.");
      }

      closeModal();
      await loadItems();
    } catch (err) {
      setActionError(err.message || "Action failed.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (item) => {
    const ok = window.confirm(`Delete "${item.name}"?`);
    if (!ok) return;

    setActionError("");
    setSuccessMessage("");

    try {
      await requestJson(`/api/admin/items/${item.itemId}`, {
        method: "DELETE",
      });
      setSuccessMessage("Item deleted successfully.");
      await loadItems();
    } catch (err) {
      setActionError(err.message || "Failed to delete item.");
    }
  };

  return (
    <div className="admin-layout">
      <AdminSidebar />

      <div className="admin-main-content">
        <AdminNavbar />

        <div className="admin-container">
          <header className="admin-header">
            <h1>Inventory Management</h1>
            <button className="btn-add-item" onClick={openCreateModal}>
              + Add New Item
            </button>
          </header>

          {successMessage && <div className="alert-success">{successMessage}</div>}
          {actionError && <div className="alert-error">{actionError}</div>}

          <section className="admin-card">
            <div className="card-header">
              <span className="icon">📦</span> All Inventory Items
            </div>

            <div className="table-responsive-wrapper">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Thumbnail</th>
                    <th>Item Name</th>
                    <th>Category</th>
                    <th>Asset Tag</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {loading && (
                    <tr>
                      <td colSpan="6">Loading inventory...</td>
                    </tr>
                  )}

                  {!loading && error && (
                    <tr>
                      <td colSpan="6">{error}</td>
                    </tr>
                  )}

                  {!loading && !error && items.length === 0 && (
                    <tr>
                      <td colSpan="6">No inventory items found.</td>
                    </tr>
                  )}

                  {!loading &&
                    !error &&
                    items.map((item) => (
                      <tr key={item.itemId}>
                        <td>
                          <img
                            src={item.imageFileUrl || "https://via.placeholder.com/50"}
                            alt={item.name}
                            className="thumb"
                          />
                        </td>
                        <td>{item.name}</td>
                        <td>{item.categoryName || "—"}</td>
                        <td>{item.assetTag || "—"}</td>
                        <td>
                          <span className={`badge ${(item.status || "").toLowerCase()}`}>
                            {item.status || "—"}
                          </span>
                        </td>
                        <td className="actions">
                          <button className="icon-btn" onClick={() => openEditModal(item)}>
                            ✏️
                          </button>
                          <button
                            className="icon-btn delete"
                            onClick={() => handleDelete(item)}
                          >
                            🗑️
                          </button>
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>

      {modalOpen && (
        <div className="inv-modal-backdrop">
          <div className="inv-modal">
            <h2>{mode === "create" ? "Add Item" : "Edit Item"}</h2>

            <form onSubmit={handleSubmit} className="inv-form">
              <label>
                Name
                <input
                  name="name"
                  value={form.name}
                  onChange={onFormChange}
                  required
                />
              </label>

              <label>
                Description
                <textarea
                  name="description"
                  value={form.description}
                  onChange={onFormChange}
                  rows={3}
                />
              </label>

              <label>
                Asset Tag
                <input
                  name="assetTag"
                  value={form.assetTag}
                  onChange={onFormChange}
                  required
                />
              </label>

              {hasCategories ? (
                <label>
                  Category
                  <select
                    name="categoryId"
                    value={form.categoryId}
                    onChange={onFormChange}
                    required
                  >
                    <option value="">Select category</option>
                    {categories.map((cat) => (
                      <option key={cat.categoryId} value={cat.categoryId}>
                        {cat.name}
                      </option>
                    ))}
                  </select>
                </label>
              ) : (
                <label>
                  Category ID
                  <input
                    name="categoryId"
                    type="number"
                    value={form.categoryId}
                    onChange={onFormChange}
                    placeholder="Enter category id"
                    required
                  />
                </label>
              )}

              <label>
                Image {mode === "create" ? "" : "(optional for update)"}
                <input
                  name="imageFile"
                  type="file"
                  accept="image/png,image/jpeg,image/webp"
                  onChange={onFormChange}
                  required={mode === "create"}
                />
              </label>

              {actionError && <div className="alert-error">{actionError}</div>}

              <div className="inv-modal-actions">
                <button type="button" onClick={closeModal} disabled={submitting}>
                  Cancel
                </button>
                <button type="submit" className="btn-add-item" disabled={submitting}>
                  {submitting
                    ? "Saving..."
                    : mode === "create"
                    ? "Create Item"
                    : "Update Item"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}