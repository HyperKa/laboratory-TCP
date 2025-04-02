package com.example.demo.dto;

import java.time.LocalDate;

public class AnalysisResultRequest {
    private String researchFile;
    private LocalDate analysisDate;
    private Long clientId;

    // Getters and Setters
    public String getResearchFile() {
        return researchFile;
    }

    public void setResearchFile(String researchFile) {
        this.researchFile = researchFile;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}