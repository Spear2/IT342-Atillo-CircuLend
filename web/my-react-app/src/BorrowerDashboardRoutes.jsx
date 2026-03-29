import React from 'react';
import { Routes, Route} from "react-router-dom";
import BorrowerDashboard from "./pages/Borrower/Dashboard/Dashboard"

export default function BorrowerDashboardRoutes(){
    return(
        <Routes>
            <Route path='/' element={<BorrowerDashboard/>} />
        </Routes>
    );
}