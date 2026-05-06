import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AdminNavbar from "../../../Components/Admin/Navbar/Navbar";
import AdminSidebar from "../../../Components/Admin/Sidebar/Sidebar";
import { getApiClient } from "../../../api/ApiClientSingleton";
import "../Dashboard/adminDashboard.css";
import "./inventoryManagementPage.css";
import AddItemModal from "../../../Components/Admin/ReusableAddItemModal/AddItemModal"

import edit from "../../../assets/edit.png"
import deleteImg from "../../../assets/delete.png"
import inventory from "../../../assets/black-inventory.png"


const ICONS = {
  header: inventory,
  edit: edit,
  delete: deleteImg,
  add: "/placeholders/icon-add.png",
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

export default function InventoryManagementPage() {
  const navigate = useNavigate();

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  const [successMessage, setSuccessMessage] = useState("");
  const [actionError, setActionError] = useState("");

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

  useEffect(() => {
    loadItems();
  }, []);

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

            {/* If you have create route, keep this. Otherwise remove temporarily. */}
            <button
              className="btn-add-item"
              type="button"
              onClick={() => setIsAddModalOpen(true)}
            >
              <img className="btn-icon-img" src={ICONS.add} alt="" />
              <span>+ Add New Item</span>
            </button>
          </header>

          {successMessage && <div className="alert-success">{successMessage}</div>}
          {actionError && <div className="alert-error">{actionError}</div>}

          <section className="admin-card">
            <div className="card-header">
              <img className="card-header-icon-img" src={ICONS.header} alt="" />
              <span>All Inventory Items</span>
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
                          <span className={`badge-${(item.status || "").toLowerCase()}`}>
                            {item.status || "—"}
                          </span>
                        </td>
                        <td className="actions">
                          <button
                            className="icon-btn"
                            type="button"
                            aria-label={`Edit ${item.name}`}
                            onClick={() => navigate(`/admin/items/${item.itemId}`)}
                          >
                            <img className="table-action-icon-img" src={ICONS.edit} alt="" />
                          </button>

                          <button
                            className="icon-btn delete"
                            type="button"
                            aria-label={`Delete ${item.name}`}
                            onClick={() => handleDelete(item)}
                          >
                            <img className="table-action-icon-img" src={ICONS.delete} alt="" />
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

      <AddItemModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onCreated={async () => {
          setIsAddModalOpen(false);
          setActionError("");
          setSuccessMessage("Item added successfully.");
          await loadItems();
        }}
      />
    </div>
  );
}