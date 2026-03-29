import React from 'react';
import { NavLink } from 'react-router-dom'; // Using NavLink for automatic "active" class
import './Sidebar.css';

const Sidebar = () => {
  return (
    <aside className="admin-sidebar">
      <div className="sidebar-header">
        Admin Dashboard
      </div>
      <nav className="sidebar-menu">
        <NavLink to="/admin/inventory" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
          <span className="icon">📊</span> Inventory
        </NavLink>
        <NavLink to="/admin/transactions" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
          <span className="icon">🔄</span> Transactions
        </NavLink>
        <NavLink to="/admin/users" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
          <span className="icon">👥</span> Users
        </NavLink>
        <NavLink to="/admin/logs" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
          <span className="icon">🛡️</span> Audit Logs
        </NavLink>
      </nav>
    </aside>
  );
};

export default Sidebar;