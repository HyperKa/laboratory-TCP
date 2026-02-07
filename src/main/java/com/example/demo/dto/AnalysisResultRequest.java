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
        this.clientId = (long) analysisResult.getClient().getId();
    }
}