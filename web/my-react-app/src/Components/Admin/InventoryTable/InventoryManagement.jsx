import React, { useEffect, useState } from "react";
import { getApiClient } from "../../../api/ApiClientSingleton";
import { useNavigate } from "react-router-dom";

import header from "../../../assets/black-inventory.png";
import edit from "../../../assets/edit.png";
import deleteIcon from "../../../assets/delete.png";

const ICONS = {
  header,
  edit,
  delete: deleteIcon,
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

const InventoryManagement = () => {
  const [inventoryItems, setInventoryItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [successMessage, setSuccessMessage] = useState("");
  const [actionError, setActionError] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const loadInventory = async () => {
    setLoading(true);
    setError("");

    try {
      const body = await requestJson("/api/items?page=0&size=3", { method: "GET" });
      setInventoryItems(body?.data?.content || []);
    } catch (err) {
      setInventoryItems([]);
      setError(err.message || "Failed to fetch inventory items.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadInventory();
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
      await loadInventory(); // refresh table
    } catch (err) {
      setActionError(err.message || "Failed to delete item.");
    }
  };

  return (
    <section className="admin-card">
      <div className="card-header">
        <img className="card-header-icon-img" src={ICONS.header} alt="" />
        <span>Inventory Management</span>
      </div>

      {successMessage && <div className="alert-success">{successMessage}</div>}
      {actionError && <div className="alert-error">{actionError}</div>}

      <table className="admin-table">
        <thead>
          <tr>
            <th>Thumbnail</th>
            <th>Item Name & Category</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {loading && (
            <tr>
              <td colSpan="4">Loading inventory...</td>
            </tr>
          )}

          {!loading && error && (
            <tr>
              <td colSpan="4">{error}</td>
            </tr>
          )}

          {!loading && !error && inventoryItems.length === 0 && (
            <tr>
              <td colSpan="4">No inventory items found.</td>
            </tr>
          )}

          {!loading &&
            !error &&
            inventoryItems.map((item) => (
              <tr key={item.itemId}>
                <td>
                  <img
                    src={item.imageFileUrl || "https://via.placeholder.com/50"}
                    alt={item.name}
                    className="thumb"
                  />
                </td>
                <td>
                  <strong>{item.name}</strong>
                  <br />
                  <small>{item.categoryName || "Uncategorized"}</small>
                </td>
                <td>
                  <span className={`badge ${(item.status || "").toLowerCase()}`}>
                    {item.status}
                  </span>
                </td>
                <td className="actions">
                  <button
                    type="button"
                    className="icon-btn"
                    aria-label={`Edit ${item.name}`}
                    onClick={() => navigate(`/admin/items/${item.itemId}`)}
                  >
                    <img className="table-action-icon-img" src={ICONS.edit} alt="" />
                  </button>

                  <button
                    type="button"
                    className="icon-btn delete"
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
    </section>
  );
};

export default InventoryManagement;