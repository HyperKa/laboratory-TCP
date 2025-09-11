import React from "react";
import ReactDOM from 'react-dom/client'
import PatientTable from "./PatientTable";

const container = document.getElementById('react-patient-table-container');
if (container) {
    const root = ReactDOM.createRoot(container)
    root.render(<PatientTable />);
}