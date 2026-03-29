import React from 'react';

const GlobalTransactions = () => {
  const transactions = [
    // { id: 1, borrower: 'John Doe', item: 'Camera Tripod', borrowDate: 'Apr 20, 2024', returnDate: 'Apr 27, 2024', status: 'Completed' },
    // { id: 2, borrower: 'Michael Brov', item: 'Dumbbells', borrowDate: 'Apr 25, 2024', returnDate: '—', status: 'Active' },
  ];

  return (
    <section className="admin-card">
      <div className="card-header">
        <span className="icon">⏳</span> Global Transaction Logs
      </div>
      <div className='table-responsive-wrapper'>
          <table className="admin-table">
        <thead>
          <tr>
            <th>Borrower</th>
            <th>Item Name</th>
            <th>Borrow Date</th>
            <th>Return Date</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map(tx => (
            <tr key={tx.id}>
              <td>{tx.borrower}</td>
              <td>{tx.item}</td>
              <td>{tx.borrowDate}</td>
              <td>{tx.returnDate}</td>
              <td><span className={`badge ${tx.status.toLowerCase()}`}>{tx.status}</span></td>
            </tr>
          ))}
        </tbody>
      </table>

      </div>
    </section>
  );
};

export default GlobalTransactions;