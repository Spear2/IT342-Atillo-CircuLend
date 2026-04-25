import React, { useEffect, useState } from "react";
import { getApiClient } from "../../../api/ApiClientSingleton";

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
        <span className="icon">🏫</span> Inventory Management
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
                  <button className="icon-btn">✏️</button>
                  <button className="icon-btn delete">🗑️</button>
                </td>
              </tr>
            ))}
        </tbody>
      </table>
    </section>
  );
};

export default InventoryManagement;