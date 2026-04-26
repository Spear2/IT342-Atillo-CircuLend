import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import AdminDashboard from "./pages/Admin/Dashboard/AdminDashboard";
import InventoryManagementPage from "./pages/Admin/Inventory/InventoryManagementPage";

export default function AdminDashboardRoutes() {
  return (
    <Routes>
      <Route path="/" element={<AdminDashboard />} />
      <Route path="/inventory" element={<InventoryManagementPage />} />
      <Route path="*" element={<Navigate to="/admin" replace />} />
    </Routes>
  );
}