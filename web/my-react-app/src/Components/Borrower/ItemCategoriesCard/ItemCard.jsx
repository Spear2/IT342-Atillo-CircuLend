import React from "react";
import { useNavigate } from "react-router-dom";
import "./ItemCard.css";


export default function ItemCard({ item }) {
  const navigate = useNavigate();

  const {
    itemId,
    name,
    categoryName,
    status = "AVAILABLE",
    imageFileUrl,
  } = item || {};

  const normalizedStatus = String(status).toUpperCase();
  const badgeClass = normalizedStatus.toLowerCase();

  const badgeLabel =
    normalizedStatus === "AVAILABLE"
      ? "Available"
      : normalizedStatus === "BORROWED"
      ? "Borrowed"
      : "Maintenance";

  return (
    <div className="item-card">
      <div className="item-image">
        <img
          src={imageFileUrl}
        
        />
      </div>

      <div className="item-info">
        <div className="item-header">
          <h3>{name || "Unnamed Item"}</h3>
          <p className="item-category">{categoryName || "Uncategorized"}</p>
        </div>

        <div className={`status-badge ${badgeClass}`}>● {badgeLabel}</div>

        <button
          className="view-details-btn"
          type="button"
          onClick={() => navigate(`/borrower/items/${itemId}`)}
          disabled={!itemId}
        >
          View Details
        </button>
      </div>
    </div>
  );
}