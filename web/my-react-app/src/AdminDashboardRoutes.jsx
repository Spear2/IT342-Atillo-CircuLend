import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import AdminDashboard from "@/features/admin/pages/AdminDashboard";
import InventoryManagementPage from "@/features/inventory/pages/InventoryManagementPage";
import AdminTransactionsPage from "@/features/transactions/pages/AdminTransactionsPage";
import AdminUsersPage from "@/features/users/pages/AdminUsersPage";
import AdminItemDetailPage from "@/features/inventory/pages/AdminItemDetailPage";
import AdminAuditLogsPage from "@/features/admin/pages/AdminAuditLogsPage";
import UserDetailPage from "@/features/users/pages/UserDetailPage";

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