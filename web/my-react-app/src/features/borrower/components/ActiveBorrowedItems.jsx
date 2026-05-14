import React from "react";
import "./ActiveBorrowedItems.css";
import Camera from "@/assets/camera.jpg"

export default function ActiveBorrowedItems({ items = [], onReturn, returningTxId }) {
  return (
    <div className="card">
      <div className="card-header">
        Active Borrowed Items
      </div>

      <div className="card-body">
        {items.length === 0 ? (
          <div className="empty-row">No active borrowed items.</div>
        ) : (
          items.map((item) => (
            <div key={item.transactionId} className="item-row">
              <div className="item-infos">
                <img
                  src={item.imageFileUrl}
                  alt={item.itemName}
                  className="item-thumb"
                />
                <div className="item-details">
                  <h4 className="item-name">{item.itemName}</h4>
                  <p className="item-category">🗂 {item.categoryName || "Uncategorized"}</p>
                  <p className="item-date">Borrowed on: {item.borrowedLabel}</p>
                </div>
              </div>

              <button
                className="btn-return"
                onClick={() => onReturn(item)}
                disabled={returningTxId === item.transactionId}
                type="button"
              >
                {returningTxId === item.transactionId ? "Returning..." : "Return Item"}
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}