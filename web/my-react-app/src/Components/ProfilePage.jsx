import React from "react";
import "../css/Profile.css";

const ProfilePage = ({ user, isLoading, error }) => {
  if (isLoading) return <div className="dashboard-content">Loading...</div>;
  if (error) return <div className="dashboard-error">{error}</div>;

  return (
    <div className="dashboard-content">
      <div className="dashboard-header">
        <h2 className="dashboard-title">My Profile</h2>
        <p className="dashboard-subtitle">Manage your account settings</p>
      </div>

      {user && (
        <div className="dashboard-grid">
          {/* Personal Info Card */}
          <div className="info-card">
            <div className="card-header">
              <h3 className="card-title">Personal Information</h3>
            </div>
            
            <div className="data-row">
              <span className="data-label">Email Address</span>
              <p className="data-value">{user.email}</p>
            </div>

            <div className="data-row">
              <span className="data-label">Mobile Contact</span>
              <p className="data-value">{user.phoneNumber || "Not provided"}</p>
            </div>

            <div className="data-row">
              <span className="data-label">Residential Address</span>
              <p className="data-value">{user.address || "Not provided"}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfilePage;