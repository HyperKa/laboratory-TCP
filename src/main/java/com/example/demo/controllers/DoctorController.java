package com.example.demo.controllers;

import com.example.demo.dto.DoctorDTO;
import com.example.demo.service.BlacklistedTokenService;
import com.example.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private com.example.demo.service.BlacklistedTokenService BlacklistedTokenService; // Внедрение сервиса



    // CREATE
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctorFromDTO(doctorDTO));
    }

    // READ (все записи)
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctorsAsDTO());
    }

    // READ (по ID)
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorByIdAsDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @RequestBody DoctorDTO updatedDoctorDTO) {
        return ResponseEntity.ok(doctorService.updateDoctorFromDTO(id, updatedDoctorDTO));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Токен не предоставлен");
        }

        // Извлекаем токен из заголовка
        String token = authHeader.substring(7); // Убираем "Bearer " из заголовка

        // Добавляем токен в черный список через внедренный сервис
        BlacklistedTokenService.addToBlacklist(token);

        return ResponseEntity.ok("Выход выполнен успешно");
    }
}