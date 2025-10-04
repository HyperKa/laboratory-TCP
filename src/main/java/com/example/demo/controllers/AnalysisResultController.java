package com.example.demo.controllers;

import com.example.demo.dto.AnalysisResultRequest;
import com.example.demo.dto.DiseaseHistoryDTO;
import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.DiseaseHistory;
import com.example.demo.service.AnalysisResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis-results")
public class AnalysisResultController {

    @Autowired
    private AnalysisResultService analysisResultService;

    // CREATE: Создание нового анализа
    @PostMapping
    public ResponseEntity<AnalysisResultRequest> createAnalysisResult(@RequestBody AnalysisResultRequest request) {
        AnalysisResult createdRecord = analysisResultService.createAnalysisResult(
            request.getResearchFile(),
            request.getAnalysisDate(),
            request.getClientId()
        );
        AnalysisResultRequest dto = analysisResultService.convertToDTO(createdRecord);
        return ResponseEntity.status(201).body(dto);
    }

    // READ: Получение всех анализов
    @GetMapping
    public ResponseEntity<List<AnalysisResult>> getAllAnalysisResults() {
        List<AnalysisResult> records = analysisResultService.getAllClients();
        return ResponseEntity.ok(records);
    }

    // READ: Получение анализа по ID
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResult> getAnalysisResultById(@PathVariable Long id) {
        AnalysisResult record = analysisResultService.getClientById(id);
        if (record != null) {
            return ResponseEntity.ok(record);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<AnalysisResultRequest> updateRecord(
            @PathVariable Long recordId,
            @RequestBody AnalysisResultRequest request) {

        AnalysisResult updatedRecord = analysisResultService.updateAnalysisResult(recordId, request);
        AnalysisResultRequest dto = analysisResultService.convertToDTO(updatedRecord);
        return ResponseEntity.ok(dto);
    }

    // DELETE: Удаление анализа
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysisResult(@PathVariable Long id) {
        analysisResultService.deleteAnalysisResult(id);
        return ResponseEntity.noContent().build();
    }
}