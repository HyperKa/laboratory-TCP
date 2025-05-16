package com.example.demo.controllers;

import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import com.example.demo.service.AppointmentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointment-records")
public class AppointmentRecordController {

    @Autowired
    private AppointmentRecordService appointmentRecordService;

    @Autowired
    private ClientRepository clientRepository;

    // Получение всех записей
    @GetMapping
    public ResponseEntity<List<AppointmentRecordDTO>> getAllRecords() {
        return ResponseEntity.ok(appointmentRecordService.getAllRecordsAsDTO());
    }

    // 🔥 Получение записей текущего клиента по его ID из токена
    @GetMapping("/my-records")
    public ResponseEntity<List<AppointmentRecordDTO>> getMyRecords(Authentication authentication) {
        Long userId;

         if (authentication.getPrincipal() instanceof Map<?, ?> claimsMap) {
            Map<String, Object> claims = (Map<String, Object>) claimsMap;
            String sub = (String) claims.get("sub");

            if (sub == null || sub.isEmpty()) {
                throw new RuntimeException("Поле 'sub' отсутствует в токене");
            }

            try {
                userId = Long.valueOf(sub);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Неверный формат ID в токене", e);
            }
        }
        else {
            throw new RuntimeException("Неизвестный тип Principal: " + authentication.getPrincipal().getClass().getName());
        }

        List<AppointmentRecordDTO> records = appointmentRecordService.findRecordsByClientId(userId);
        return ResponseEntity.ok(records);
    }

    // Получение записи по ID
    @GetMapping("/{recordId}")
    public ResponseEntity<AppointmentRecordDTO> getRecordById(@PathVariable Integer recordId) {
        return appointmentRecordService.getRecordByIdAsDTO(recordId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Создание новой записи
    @PostMapping
    public ResponseEntity<AppointmentRecordDTO> createRecord(@RequestBody AppointmentRecordDTO dto) {
        AppointmentRecord createdRecord = appointmentRecordService.createRecordFromDTO(dto);
        return ResponseEntity.status(201).body(appointmentRecordService.convertToDTO(createdRecord));
    }

    // Обновление записи
    @PutMapping("/{recordId}")
    public ResponseEntity<AppointmentRecordDTO> updateRecord(
            @PathVariable Integer recordId,
            @RequestBody AppointmentRecordDTO dto) {

        AppointmentRecord updatedRecord = appointmentRecordService.updateRecordFromDTO(recordId, dto);
        return ResponseEntity.ok(appointmentRecordService.convertToDTO(updatedRecord));
    }

    // Удаление записи
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Integer recordId) {
        appointmentRecordService.deleteRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}