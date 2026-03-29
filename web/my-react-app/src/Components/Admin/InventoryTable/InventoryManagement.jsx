import React from 'react';

const InventoryManagement = () => {
  const inventoryItems = [
    // { id: 1, name: 'Canon DSLR', category: 'Technology', status: 'Available', img: 'https://via.placeholder.com/50' },
    // { id: 2, name: 'Basketball', category: 'Sports', status: 'Borrowed', img: 'https://via.placeholder.com/50' },
  ];

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
          {inventoryItems.map(item => (
            <tr key={item.id}>
              <td><img src={item.img} alt={item.name} className="thumb" /></td>
              <td><strong>{item.name}</strong><br/><small>{item.category}</small></td>
              <td><span className={`badge ${item.status.toLowerCase()}`}>{item.status}</span></td>
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