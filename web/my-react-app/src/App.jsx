import { BrowserRouter } from "react-router-dom";
import React from 'react'; // Add this if it's missing
import WebRoutes from "./WebRoutes";

function App() {


  return (
    
      <BrowserRouter>
        <WebRoutes/>
      </BrowserRouter>
      
   
  );
}

export default App
