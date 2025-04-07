package com.example.demo.controllers;

import com.example.demo.dto.DiseaseHistoryDTO;
import com.example.demo.entity.DiseaseHistory;
import com.example.demo.service.DiseaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/disease-history")
public class DiseaseHistoryController {

    @Autowired
    private DiseaseHistoryService diseaseHistoryService;

    /*
    // CREATE: Создание новой истории болезни
    @PostMapping
    public ResponseEntity<DiseaseHistoryDTO> createDiseaseHistory(
            @RequestParam Long doctorId,
            @RequestParam String firstNameDoctor,
            @RequestParam String lastNameDoctor,
            @RequestParam String profession,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam String disease) {

        DiseaseHistory createdRecord = diseaseHistoryService.createRecord(
                doctorId, firstNameDoctor, lastNameDoctor, profession, startDate, endDate, disease);
        DiseaseHistoryDTO dto = diseaseHistoryService.convertToDTO(createdRecord);
        return ResponseEntity.status(201).body(dto);
    }
    */

    @PostMapping
    public ResponseEntity<DiseaseHistory> createDiseaseHistory(@RequestBody DiseaseHistoryDTO request) {
        DiseaseHistory createdRecord = diseaseHistoryService.createRecord(request);
        return ResponseEntity.status(201).body(createdRecord);
    }
    // READ: Получение всех историй болезни
    @GetMapping
    public ResponseEntity<List<DiseaseHistoryDTO>> getAllRecords() {
        List<DiseaseHistory> records = (List<DiseaseHistory>) diseaseHistoryService.getAllRecords();
        List<DiseaseHistoryDTO> dtos = records.stream()
                .map(diseaseHistoryService::convertToDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // READ: Получение истории болезни по ID
    @GetMapping("/{recordId}")
    public ResponseEntity<DiseaseHistoryDTO> getRecordById(@PathVariable int recordId) {
        return diseaseHistoryService.getRecordById(recordId)
                .map(diseaseHistoryService::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    // UPDATE: Обновление истории болезни
    @PutMapping("/{recordId}")
    public ResponseEntity<DiseaseHistoryDTO> updateRecord(
            @PathVariable int recordId,
            @RequestParam Long doctorId,
            @RequestParam String firstNameDoctor,
            @RequestParam String lastNameDoctor,
            @RequestParam String profession,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam String disease) {

        DiseaseHistory updatedRecord = diseaseHistoryService.updateRecord(
                recordId, doctorId, firstNameDoctor, lastNameDoctor, profession, startDate, endDate, disease);
        DiseaseHistoryDTO dto = diseaseHistoryService.convertToDTO(updatedRecord);
        return ResponseEntity.ok(dto);
    }
    */

    @PutMapping("/{recordId}")
    public ResponseEntity<DiseaseHistoryDTO> updateRecord(
            @PathVariable int recordId,
            @RequestBody DiseaseHistoryDTO request) {

        DiseaseHistory updatedRecord = diseaseHistoryService.updateRecord(recordId, request);
        DiseaseHistoryDTO dto = diseaseHistoryService.convertToDTO(updatedRecord);
        return ResponseEntity.ok(dto);
    }
    // DELETE: Удаление истории болезни
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable int recordId) {
        diseaseHistoryService.deleteRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}