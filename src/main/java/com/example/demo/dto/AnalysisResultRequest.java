package com.example.demo.dto;

import com.example.demo.entity.AnalysisResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResultRequest {
    private Long recordId;
    private String researchFile;
    private LocalDate analysisDate;
    private Long clientId;

    public AnalysisResultRequest(AnalysisResult analysisResult) {
        this.recordId = analysisResult.getRecordId();
        this.researchFile = analysisResult.getResearchFile();
        this.analysisDate = analysisResult.getAnalysisDate();
        this.clientId = analysisResult.getClient().getId();
    }
    /*

    public AnalysisResultRequest(AnalysisResult analysisResult) {
    }

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

     */
}