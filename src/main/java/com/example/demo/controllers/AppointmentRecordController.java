package com.example.demo.controllers;

import com.example.demo.entity.AppointmentRecord;
import com.example.demo.service.AppointmentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment_records")
public class AppointmentRecordController {

    @Autowired
    private AppointmentRecordService appointmentRecordService;

    // CREATE
    @PostMapping
    public ResponseEntity<AppointmentRecord> createRecord(@RequestBody AppointmentRecord record) {
        return ResponseEntity.status(201).body(appointmentRecordService.createRecord(
                (long) record.getClient().getId(),
                (long) record.getDoctor().getId(),
                record.getAppointmentDate(),
                record.getAppointmentTime(),
                record.getServiceName(),
                record.getDiseaseHistory() != null ? (long) record.getDiseaseHistory().getRecordId() : null
        ));
    }

    // READ (все записи)
    @GetMapping
    public ResponseEntity<List<AppointmentRecord>> getAllRecords() {
        return ResponseEntity.ok(appointmentRecordService.getAllRecords());
    }

    // READ (по составному ключу)
    @GetMapping("/{clientId}/{recordId}")
    public ResponseEntity<AppointmentRecord> getRecordById(
            @PathVariable Long clientId,
            @PathVariable Long recordId) {

        return appointmentRecordService.getRecordById(clientId, recordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{clientId}/{recordId}")
    public ResponseEntity<AppointmentRecord> updateRecord(
            @PathVariable Long clientId,
            @PathVariable Long recordId,
            @RequestBody AppointmentRecord updatedRecord) {

        return ResponseEntity.ok(appointmentRecordService.updateAppointmentRecord(clientId, recordId, updatedRecord));
    }

    // DELETE
    @DeleteMapping("/{clientId}/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long clientId,
            @PathVariable Long recordId) {

        appointmentRecordService.deleteRecord(clientId, recordId);
        return ResponseEntity.noContent().build();
    }
}