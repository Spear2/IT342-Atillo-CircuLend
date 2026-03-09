import React from "react";
import { NavLink } from "react-router-dom";

const SideBar = ({ onLogout }) => {
  const linkClass = ({ isActive }) =>
    `sidebar-link${isActive ? " sidebar-link--active" : ""}`;

  return (
    <aside className="dashboard-sidebar">
      <div className="sidebar-brand">My App</div>
      <nav className="sidebar-nav">
        <NavLink to="/dashboard" end className={linkClass}>
          Dashboard
        </NavLink>
        <NavLink to="/dashboard/profile" className={linkClass}>
          Profile
        </NavLink>
      </nav>
      <button className="sidebar-logout" onClick={onLogout} type="button">
        Logout
      </button>
    </aside>
  );
};

export default SideBar;
