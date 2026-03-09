// Dashboard.jsx
import React, { useEffect, useState } from "react";
import { useNavigate, Routes, Route } from "react-router-dom";
import { getAuthHeader, getToken, logout } from "../security/auth";
import "../css/dashboard.css";
import DashboardPage from "../Components/DashboardPage";
import ProfilePage from "../Components/ProfilePage";
import SideBar from "../Components/NavigationBar/SideBar";

const Dashboard = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    const fetchProfile = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/user/me`, {
          method: "GET",
          headers: { ...getAuthHeader(), "Content-Type": "application/json" },
        });

        if (!response.ok) {
          if (response.status === 401) logout(); 
          const message = await response.text();
          throw new Error(message || "Unable to load profile.");
        }

        const data = await response.json();
        setUser(data);
      } catch (err) {
        setError(err.message || "Unable to load profile.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchProfile();
  }, [navigate]);

  const handleLogout = () => {
    logout();
    navigate("/", { replace: true });
  };

  return (
    <div className="dashboard-page"> 
      <div className="dashboard-card"> 
        
        <SideBar onLogout={handleLogout} />
        <Routes>
          <Route path="/" element={<DashboardPage user={user} isLoading={isLoading} error={error} />} />
          <Route path="/profile" element={<ProfilePage user={user} isLoading={isLoading} error={error} />} />
        </Routes>
      
      </div>
    </div>
  );
};

export default Dashboard;