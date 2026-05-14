import React from 'react';
import { Routes, Route} from "react-router-dom";
import BorrowerDashboard from "@/features/borrower/pages/Dashboard"
import ExploreItemsPage from "@/features/borrower/pages/ExploreItemsPage"
import ItemDetailPage from '@/features/borrower/pages/ItemDetailPage';


export default function BorrowerDashboardRoutes(){
    return(
        <Routes>
            <Route path='/' element={<ExploreItemsPage/>} />
            <Route path='/dashboard' element={<BorrowerDashboard/>} />
            
            <Route path="/items/:id" element={<ItemDetailPage />} />
        </Routes>
    );
}