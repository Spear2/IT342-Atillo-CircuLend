import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AdminNavbar from "../../../Components/Admin/Navbar/Navbar";
import AdminSidebar from "../../../Components/Admin/Sidebar/Sidebar";
import InventoryManagement from "../../../Components/Admin/InventoryTable/InventoryManagement";
import GlobalTransactions from "../../../Components/Admin/TransactionTable/GlobalTransactions";
import UserManagementTable from "../../../Components/Admin/UserManagementTable/UserManagementTable";
import AddItemModal from "../../../Components/Admin/ReusableAddItemModal/AddItemModal"
import "./adminDashboard.css";

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-main-content">
        <AdminNavbar />

        <div className="admin-container">
          <header className="admin-header">
            <h1>Admin Overview</h1>
            <button className="btn-add-item" onClick={() => setIsAddModalOpen(true)}>
              + Add New Item
            </button>
          </header>

          {successMessage && <div className="alert-success">{successMessage}</div>}

          <InventoryManagement />
          <GlobalTransactions />
          <UserManagementTable />
        </div>
      </div>

      <AddItemModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onCreated={() => {
          setIsAddModalOpen(false);
          setSuccessMessage("Item added successfully.");
          navigate("/admin/inventory");
        }}
      />
    </div>
  );
};

export default AdminDashboard;