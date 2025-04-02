package com.example.demo.service;

import com.example.demo.dto.DiseaseHistoryDTO;
import com.example.demo.entity.DiseaseHistory;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.DiseaseHistoryRepository;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DiseaseHistoryService {

    @Autowired
    private DiseaseHistoryRepository diseaseHistoryRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // Получение всех записей истории болезни
    public Iterable<DiseaseHistory> getAllRecords() {
        return diseaseHistoryRepository.findAll();
    }

    // Получение записи по ID
    public Optional<DiseaseHistory> getRecordById(int recordId) {
        return diseaseHistoryRepository.findById(recordId);
    }

    // Создание новой записи истории болезни
    public DiseaseHistory createRecord(Long doctorId, String firstNameDoctor, String lastNameDoctor, String profession,
            LocalDateTime startDate, LocalDateTime endDate, String disease) {

        // Находим врача по ID
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Врач с ID " + doctorId + " не найден"));

        // Создаем объект истории болезни
        DiseaseHistory history = new DiseaseHistory();
        history.setDoctor(doctor);
        history.setFirstNameDoctor(firstNameDoctor);
        history.setLastNameDoctor(lastNameDoctor);
        history.setProfession(profession);
        history.setStartDate(startDate);
        history.setEndDate(endDate);
        history.setDisease(disease);

        // Сохраняем запись
        return diseaseHistoryRepository.save(history);
    }

    // Обновление записи истории болезни
    public DiseaseHistory updateRecord(
            int recordId,
            Long doctorId,
            String firstNameDoctor,
            String lastNameDoctor,
            String profession,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String disease) {

        // Находим существующую запись
        DiseaseHistory history = diseaseHistoryRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Запись с ID " + recordId + " не найдена"));

        // Находим врача по ID
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Врач с ID " + doctorId + " не найден"));

        // Обновляем поля
        history.setDoctor(doctor);
        history.setFirstNameDoctor(firstNameDoctor);
        history.setLastNameDoctor(lastNameDoctor);
        history.setProfession(profession);
        history.setStartDate(startDate);
        history.setEndDate(endDate);
        history.setDisease(disease);

        // Сохраняем изменения
        return diseaseHistoryRepository.save(history);
    }

    // Удаление записи
    public void deleteRecord(int recordId) {
        diseaseHistoryRepository.deleteById(recordId);
    }

    // Преобразование Entity -> DTO
    public DiseaseHistoryDTO convertToDTO(DiseaseHistory history) {
        DiseaseHistoryDTO dto = new DiseaseHistoryDTO();
        dto.setRecordId(history.getRecordId());
        dto.setFirstNameDoctor(history.getFirstNameDoctor());
        dto.setLastNameDoctor(history.getLastNameDoctor());
        dto.setProfession(history.getProfession());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setDisease(history.getDisease());
        return dto;
    }

    // Преобразование DTO -> Entity
    private DiseaseHistory convertToEntity(DiseaseHistoryDTO dto) {
        DiseaseHistory history = new DiseaseHistory();
        history.setRecordId(dto.getRecordId());
        history.setFirstNameDoctor(dto.getFirstNameDoctor());
        history.setLastNameDoctor(dto.getLastNameDoctor());
        history.setProfession(dto.getProfession());
        history.setStartDate(dto.getStartDate());
        history.setEndDate(dto.getEndDate());
        history.setDisease(dto.getDisease());
        return history;
    }

}