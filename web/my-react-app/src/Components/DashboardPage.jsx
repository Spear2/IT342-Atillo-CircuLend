import React from "react";

const DashboardPage = ({ user, isLoading, error }) => {
  if (isLoading) return <div className="dashboard-content">Loading data...</div>;
  if (error) return <div className="dashboard-error">{error}</div>;

  // Simulate a "Join Date" for display purposes if not in DB
  const joinDate = new Date().toLocaleDateString();

  return (
    <div className="dashboard-content">
      {/* 1. Header (Top Bar in image) */}
      <div className="dashboard-header">
        <div>
            <h2 className="dashboard-title">Dashboard</h2>
        </div>
      
      </div>
    </div>
  );
};

export default DashboardPage;