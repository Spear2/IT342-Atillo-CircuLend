import React, { useEffect, useState } from "react";
import { getApiClient } from "../../../api/ApiClientSingleton";

import header from "../../../assets/black-inventory.png"
import edit from "../../../assets/edit.png"
import deleteIcon from "../../../assets/delete.png"

const ICONS = {
  header: header,
  edit: edit,
  delete: deleteIcon,
};

const InventoryManagement = () => {
  const [inventoryItems, setInventoryItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadInventory() {
      setLoading(true);
      setError("");

      try {
        const res = await getApiClient().request("/api/items?page=0&size=3", {
          method: "GET",
        });

        const body = await res.json();
        const rows = body?.data?.content || [];

        if (!cancelled) {
          setInventoryItems(rows);
        }
      } catch (err) {
        if (!cancelled) {
          setInventoryItems([]);
          setError(err.message || "Failed to fetch inventory items.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadInventory();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <section className="admin-card">
      <div className="card-header">
        <img className="card-header-icon-img" src={ICONS.header} alt="" />
        <span>Inventory Management</span>
      </div>

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
                  <button type="button" className="icon-btn" aria-label={`Edit ${item.name}`}>
                    <img className="table-action-icon-img" src={ICONS.edit} alt="" />
                  </button>
                  <button
                    type="button"
                    className="icon-btn delete"
                    aria-label={`Delete ${item.name}`}
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