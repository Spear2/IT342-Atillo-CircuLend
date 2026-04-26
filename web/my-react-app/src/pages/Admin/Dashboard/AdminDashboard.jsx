import React from 'react';
import AdminNavbar from '../../../Components/Admin/Navbar/Navbar';
import AdminSidebar from '../../../Components/Admin/Sidebar/Sidebar';
import Footer from '../../../Components/Shared/Footer/Footer'
import InventoryManagement from '../../../Components/Admin/InventoryTable/InventoryManagement';
import GlobalTransactions from '../../../Components/Admin/TransactionTable/GlobalTransactions';
import UserManagementTable from '../../../Components/Admin/UserManagementTable/UserManagementTable';
import { useNavigate } from "react-router-dom";
import './adminDashboard.css';

const AdminDashboard = () => {
  const navigate = useNavigate();

  const navigateToInvetoryAddItem = () =>{
    navigate("/admin/inventory");
  }

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-main-content">
        <AdminNavbar />
        
        <div className="admin-container">
          <header className="admin-header">
            <h1>Admin Overview</h1>
            <button className="btn-add-item" onClick={navigateToInvetoryAddItem}>+ Add New Item</button>
          </header>

          {/* You can now easily toggle these or put them on separate routes */}
          <InventoryManagement />
          <GlobalTransactions />
          <UserManagementTable />
        </div>
      </div>
      
    </div>
  );
};

export default AdminDashboard;