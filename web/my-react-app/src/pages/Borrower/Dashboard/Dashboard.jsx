import React from 'react';
import './Dashboard.css';
import BorrowerNavbar from '../../../Components/Borrower/BorrowerNavbar/Navbar';
import ActiveBorrowedItems from '../../../Components/Borrower/ActiveBorrowedItems/ActiveBorrowedItems';
import TransactionHistory from '../../../components/Borrower/TransactionHistory/TransactionHistory';
import Footer from '../../../components/Shared/Footer/Footer';

const BorrowerDashboard = () => {
  return (
    <div className="dashboard-wrapper">
      <BorrowerNavbar />
      
      <main className="dashboard-container">
        <h1 className="dashboard-title">Dashboard</h1>
        
        <section className="dashboard-section">
          <ActiveBorrowedItems />
        </section>

        <section className="dashboard-section">
          <TransactionHistory />
        </section>
      </main>

      <Footer />
    </div>
  );
};

export default BorrowerDashboard;