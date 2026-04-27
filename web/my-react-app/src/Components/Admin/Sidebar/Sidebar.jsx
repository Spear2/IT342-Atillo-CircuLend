import React from "react";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";
import overview from "../../../assets/overview.png"
import log from "../../../assets/log.png"
import user from "../../../assets/multiple-users-silhouette.png"
import transaction from "../../../assets/transaction.png"
import inventory from "../../../assets/inventory.png"
const ICONS = {
  overview: overview,
  inventory: inventory,
  transactions: transaction,
  users: user,
  logs: log,
};

const Sidebar = () => {
  const navClass = ({ isActive }) => (isActive ? "menu-item active" : "menu-item");

  return (
    <aside className="admin-sidebar">
      <div className="sidebar-header">Admin Dashboard</div>

      <nav className="sidebar-menu">
        <NavLink to="/admin" end className={navClass}>
          <img className="side-icon" src={ICONS.overview} alt="" />
          <span>Overview</span>
        </NavLink>

        <NavLink to="/admin/inventory" className={navClass}>
          <img className="side-icon" src={ICONS.inventory} alt="" />
          <span>Inventory</span>
        </NavLink>

        <NavLink to="/admin/transactions" className={navClass}>
          <img className="side-icon" src={ICONS.transactions} alt="" />
          <span>Transactions</span>
        </NavLink>

        <NavLink to="/admin/users" className={navClass}>
          <img className="side-icon" src={ICONS.users} alt="" />
          <span>Users</span>
        </NavLink>

        <NavLink to="/admin/logs" className={navClass}>
          <img className="side-icon" src={ICONS.logs} alt="" />
          <span>Audit Logs</span>
        </NavLink>
      </nav>
    </aside>
  );
};

export default Sidebar;