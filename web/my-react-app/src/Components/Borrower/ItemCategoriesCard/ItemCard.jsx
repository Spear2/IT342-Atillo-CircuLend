import React from 'react';
import './itemCard.css';

const ItemCard = ({ item }) => {
  // Destructure the item object based on your ItemResponseDTO
  const { name, categoryName, status, imageUrl } = item;

  return (
    <div className="item-card">
      <div className="item-image">
        <img 
          src={imageUrl || 'https://via.placeholder.com/280x200?text=No+Image'} 
          alt={name} 
        />
      </div>
      <div className="item-info">
        <div className="item-header">
          <h3>{name}</h3>
          <p className="item-category">{categoryName}</p>
        </div>
        
        <div className={`status-badge ${status.toLowerCase()}`}>
          {status === 'AVAILABLE' ? '● Available' : '● Borrowed'}
        </div>

        <button className="view-details-btn">
          View Details
        </button>
      </div>
    </div>
  );
};

export default ItemCard;