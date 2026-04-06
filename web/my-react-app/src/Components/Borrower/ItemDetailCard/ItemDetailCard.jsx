import React from "react";
import { useNavigate } from "react-router-dom";
import "./ItemDetailCard.css";

function statusLabel(status) {
  const s = String(status || "").toUpperCase();
  if (s === "AVAILABLE") return "Available";
  if (s === "BORROWED") return "Borrowed";
  return "Maintenance";
}

export default function SimilarItemCard({ item }) {
  const navigate = useNavigate();

  return (
    <div className="similar-card">
      <img
        src={item.imageFileUrl || "https://via.placeholder.com/260x150?text=No+Image"}
        alt={item.name || "Item"}
      />

      <div className="similar-content">
        <h4>{item.name || "Unnamed Item"}</h4>
        <p>{item.categoryName || "Uncategorized"}</p>

        <span className={`mini-status ${String(item.status || "").toLowerCase()}`}>
          ● {statusLabel(item.status)}
        </span>

        <button
          type="button"
          onClick={() => navigate(`/borrower/items/${item.itemId}`)}
        >
          View Details
        </button>
      </div>
    </div>
  );
}