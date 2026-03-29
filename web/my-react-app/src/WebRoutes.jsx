import { Routes, Route, Navigate } from "react-router-dom";
import { getToken, getRole } from "./security/auth";
import LandingPage from "./pages/Landing/LandingPage";
import LoginPage from "./pages/Authentication/LoginPage";
import RegisterPage from "./pages/Authentication/RegisterPage";
import BorrowerDashboard from "./BorrowerDashboardRoutes";
import AdminDashboard from "./AdminDashboardRoutes"

function RoleProtectedRoute({ allowedRoles, children }) {
  const token = getToken();
  const role = (getRole() || "").toUpperCase();
  if (!token) return <Navigate to="/login" replace />;
  if (!allowedRoles.includes(role)) return <Navigate to="/unauthorized" replace />;
  return children;
}

function WebRoutes() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="*" element={<Navigate to="/login" replace />} />


      <Route
        path="/borrower/*"
        element={
          <RoleProtectedRoute allowedRoles={["BORROWER"]}>
            <BorrowerDashboard />
          </RoleProtectedRoute>
        }
      />
      
      <Route 
        path="/admin/*" 
        element={
          <RoleProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminDashboard/>
          </RoleProtectedRoute>
        }/>
    </Routes>
  );
}

export default WebRoutes;