import React from "react";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";

const Sidebar = () => {
  const navClass = ({ isActive }) => (isActive ? "menu-item active" : "menu-item");

  return (
    <aside className="admin-sidebar">
      <div className="sidebar-header">Admin Dashboard</div>

      <nav className="sidebar-menu">
        <NavLink to="/admin" end className={navClass}>
          <span className="icon">📊</span> Overview
        </NavLink>

        <NavLink to="/admin/inventory" className={navClass}>
          <span className="icon">📦</span> Inventory
        </NavLink>

        <NavLink to="/admin/transactions" className={navClass}>
          <span className="icon">🔄</span> Transactions
        </NavLink>

        <NavLink to="/admin/users" className={navClass}>
          <span className="icon">👥</span> Users
        </NavLink>

        <NavLink to="/admin/logs" className={navClass}>
          <span className="icon">🛡️</span> Audit Logs
        </NavLink>
      </nav>
    </aside>
  );
};

export default Sidebar;