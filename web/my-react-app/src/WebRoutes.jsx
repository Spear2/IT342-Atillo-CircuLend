import { Routes, Route, Navigate } from "react-router-dom";
import { getToken, getRole } from "@/shared/security/auth";
import LandingPage from "@/features/public/pages/LandingPage";
import LoginPage from "@/features/auth/pages/LoginPage";
import RegisterPage from "@/features/auth/pages/RegisterPage";
import BorrowerDashboard from "./BorrowerDashboardRoutes";
import AdminDashboard from "./AdminDashboardRoutes"
import VerifyEmailPage from "@/features/auth/pages/EmailVerificationPage";
import OAuth2SuccessPage from "@/features/auth/pages/OAuth2SuccessPage";



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
      <Route path="/verify-email" element={<VerifyEmailPage/>}/>
      <Route path="*" element={<Navigate to="/login" replace />} />
      <Route path="/oauth2/success" element={<OAuth2SuccessPage />} />


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