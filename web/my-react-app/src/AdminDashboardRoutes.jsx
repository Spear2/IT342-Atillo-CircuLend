import React from 'react';
import { Routes, Route, useNavigate } from "react-router-dom";
import * as auth from "./security/auth"
import AdminDashboard from './pages/Admin/Dashboard/AdminDashboard';"./pages/Admin/Dashboard/AdminDashboard"

export default function AdminDashboardRoutes(){

    const navigate = useNavigate();

    const handleLogout = () =>{
        auth.logout();
        navigate('/login');
    }
    return(
        <Routes>
            <Route path='/' element={< AdminDashboard />}/>
        </Routes>
    );
}