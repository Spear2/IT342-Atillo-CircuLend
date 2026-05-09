import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import AdminDashboard from "./pages/Admin/Dashboard/AdminDashboard";
import InventoryManagementPage from "./pages/Admin/Inventory/InventoryManagementPage";
import AdminTransactionsPage from "./pages/Admin/Transactions/AdminTransactionsPage";
import AdminUsersPage from "./pages/Admin/Users/AdminUsersPage";
import AdminItemDetailPage from "./pages/Admin/Inventory/AdminItemDetailPage";
import AdminAuditLogsPage from "./pages/Admin/AuditLogs/AdminAuditLogsPage";
import UserDetailPage from "./pages/Admin/Users/UserDetailPage";

export default function AdminDashboardRoutes() {
  return (
    <Routes>
      <Route path="/" element={<AdminDashboard />} />
      <Route path="/inventory" element={<InventoryManagementPage />} />
      <Route path="/transactions" element={<AdminTransactionsPage />} />
      <Route path="/users" element={<AdminUsersPage />} />
      <Route path="/logs" element={<AdminAuditLogsPage />} />
      <Route path="*" element={<Navigate to="/admin" replace />} />
      <Route path="/items/:id" element={<AdminItemDetailPage />} />
      <Route path="/users/:id" element={<UserDetailPage />} />
    </Routes>
  );
}