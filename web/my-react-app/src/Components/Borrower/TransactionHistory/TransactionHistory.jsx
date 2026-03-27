import React from 'react';

const TransactionHistory = () => {
  const transactions = Array(0).fill({
    // name: 'Camera',
    // category: 'Technology',
    // borrowed: 'Nov 25, 2024',
    // returned: 'Nov 27, 2024',
    // status: 'Completed'
  });

  return (
    <div className="card no-padding">
      <div className="card-header">Transaction History</div>
      <table className="transaction-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Date Borrowed ▾</th>
            <th>Date Returned ▾</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx, idx) => (
            <tr key={idx}>
              <td>
                <div className="td-item">
                  <img src="https://via.placeholder.com/40" alt="thumb" />
                  <div>
                    <p className="bold">{tx.name}</p>
                    <p className="item-category">📁 {tx.category}</p>
                  </div>
                </div>
              </td>
              <td>{tx.borrowed}</td>
              <td>{tx.returned}</td>
              <td><span className="status-badge completed">{tx.status}</span></td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="pagination">
        <button>&lt;</button>
        <button className="active">1</button>
        <button>2</button>
        <button>3</button>
        <button>&gt;</button>
      </div>
    </div>
  );
};

export default TransactionHistory;