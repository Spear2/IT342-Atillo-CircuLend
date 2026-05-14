import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AdminNavbar from "@/features/admin/components/AdminNavbar";
import AdminSidebar from "@/features/admin/components/AdminSidebar";
import InventoryManagement from "@/features/inventory/components/InventoryManagement";
import GlobalTransactions from "@/features/transactions/components/GlobalTransactions";
import UserManagementTable from "@/features/users/components/UserTable";
import AddItemModal from "@/features/inventory/components/AddItemModal"
import "./AdminDashboard.css";

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