import React from 'react';
import ReactDOM from 'react-dom/client';
import PatientManager from "./PatientManager";
import AppointmentRecordManager from './AppointmentRecordManager';
import AnalysisResultManager from "./AnalysisResultManager";
import DiseaseHistoryManager from "./DiseaseHistoryManager";
import DoctorManager from "./DoctorManager";

{/*
const container = document.getElementById('react-patient-manager-container');
if (container) {
    const root = ReactDOM.createRoot(container);
    root.render(<PatientManager />);
}
*/}

const renderComponent = (Component, containerId) => {
    const container = document.getElementById(containerId);
    if (container) {
        const root = ReactDOM.createRoot(container);
        root.render(<Component />);
    }
}

renderComponent(PatientManager, 'react-patient-manager-container');
renderComponent(AppointmentRecordManager, 'react-appointment-record-manager-container');
renderComponent(AnalysisResultManager, 'react-analysis-result-manager-container');
renderComponent(DiseaseHistoryManager, 'react-disease-history-manager-container');
renderComponent(DoctorManager, 'react-doctor-manager-container');