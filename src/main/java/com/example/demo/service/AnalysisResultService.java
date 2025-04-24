package com.example.demo.service;

/*
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
*/

import com.example.demo.dto.AnalysisResultRequest;
import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.dto.ClientDTO;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.AnalysisResultRepository;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class AnalysisResultService {

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Преобразование Entity -> DTO
    public AnalysisResultRequest convertToDTO(AnalysisResult analysisResult) {
        return new AnalysisResultRequest(analysisResult);
    }

    // Преобразование DTO -> Entity
    public AnalysisResult convertToEntity(AnalysisResultRequest dto) {
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setRecordId(dto.getRecordId());
        analysisResult.setResearchFile(dto.getResearchFile());
        analysisResult.setAnalysisDate(dto.getAnalysisDate());
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден с ID: " + dto.getClientId()));
            analysisResult.setClient(client);
        }

        return analysisResult;
    }

    public List<AnalysisResult> getAllClients() {
        return analysisResultRepository.findAll();
    }

    public AnalysisResult getClientById(Long id) {
        return analysisResultRepository.findById(id).orElse(null);
    }

    public void deleteClient(Long id) {
        analysisResultRepository.deleteById(id);
    }

    // получение списка столбцов
    public List<String> getClientColumns() {
        return Arrays.stream(AnalysisResult.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    // Создание анализа "в одну строку"
    public AnalysisResult createAnalysisResult(String researchFile, LocalDate analysisDate, Long clientId) {
        // Находим клиента по ID
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент с ID " + clientId + " не найден"));

        // Создаем новый анализ
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setResearchFile(researchFile);
        analysisResult.setAnalysisDate(analysisDate);
        analysisResult.setClient(client);

        // Сохраняем анализ
        return analysisResultRepository.save(analysisResult);
    }


    // Обновление записи из DTO
    public AnalysisResult updateAnalysisResult(Long recordId, AnalysisResultRequest updatedDto) {
        AnalysisResult existingRecord = analysisResultRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with ID: " + recordId));

        // Обновляем поля существующей записи

        if (updatedDto.getClientId() != null) {      // <--- вас конечно не должно ничего смущать
            existingRecord.setResearchFile(updatedDto.getResearchFile());
        }
        if (updatedDto.getAnalysisDate() != null) {
            existingRecord.setAnalysisDate(updatedDto.getAnalysisDate());
        }
        if (updatedDto.getClientId() != null) {     // <--- тут тоже
            Client client = clientRepository.findById(updatedDto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден с ID: " + updatedDto.getClientId()));
            // Оказывается это .orElseThrow обязательно, так JPA запрос может быть пустым, а Client требует корректной обработки
            existingRecord.setClient(client);
        }

        return analysisResultRepository.save(existingRecord);
    }

}