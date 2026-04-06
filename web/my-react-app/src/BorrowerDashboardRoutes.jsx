import React from 'react';
import { Routes, Route} from "react-router-dom";
import BorrowerDashboard from "./pages/Borrower/Dashboard/Dashboard"
import ExploreItemsPage from "./pages/Borrower/ExploreItems/ExploreItemsPage"
import ItemDetailPage from './pages/Borrower/ItemDetail/ItemDetailPage';


export default function BorrowerDashboardRoutes(){
    return(
        <Routes>
            <Route path='/' element={<ExploreItemsPage/>} />
            <Route path='/dashboard' element={<BorrowerDashboard/>} />
            
            <Route path="/items/:id" element={<ItemDetailPage />} />
        </Routes>
    );
}