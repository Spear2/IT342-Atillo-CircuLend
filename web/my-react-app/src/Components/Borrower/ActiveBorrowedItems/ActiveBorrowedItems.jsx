import React from 'react';

const ActiveBorrowedItems = () => {
  const activeItems = [
    // { id: 1, name: 'Camera', category: 'Technology', date: 'April 25, 2025', img: 'https://via.placeholder.com/100' },
    // { id: 2, name: 'Basketball', category: 'Sports', date: 'April 25, 2025', img: 'https://via.placeholder.com/100' },
    // { id: 3, name: 'Camera', category: 'Technology', date: 'April 25, 2025', img: 'https://via.placeholder.com/100' },
  ];

  return (
    <div className="card">
      <div className="card-header">
        <span className="icon">📋</span> Active Borrowed Items
      </div>
      <div className="card-body">
        {activeItems.map((item) => (
          <div key={item.id} className="item-row">
            <div className="item-info">
              <img src={item.img} alt={item.name} className="item-thumb" />
              <div>
                <h4 className="item-name">{item.name}</h4>
                <p className="item-category">📁 {item.category}</p>
                <p className="item-date">Borrowed on: {item.date}</p>
              </div>
            </div>
            <button className="btn-return">Return Item</button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ActiveBorrowedItems;