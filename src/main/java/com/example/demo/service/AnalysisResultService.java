package com.example.demo.service;

/*
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
*/

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.AnalysisResultRepository;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    public void deleteAnalysisResult(Long id) {
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

    // Создание анализа "в одну строку"
    public AnalysisResult createAnalysisResultAsDto(AnalysisResultRequest dto, String username) {
        // Находим клиента по ID
        Client client;
        if (dto.getClientId() != null) {
            client = clientRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("Клиент с именем " + username + " не найден"));
        } else {
            client = clientRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("Клиент с именем " + username + " не найден"));
        }
        // Создаем новый анализ
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setResearchFile(dto.getResearchFile());
        analysisResult.setAnalysisDate(dto.getAnalysisDate());
        analysisResult.setClient(client);

        // Сохраняем анализ
        return analysisResultRepository.save(analysisResult);
    }

    // Получение записи по ID в виде DTO
    public Optional<AnalysisResultRequest> getRecordByIdAsDTO(Long recordId) {
        return analysisResultRepository.findById(recordId).map(this::convertToDTO);
    }


    // Обновление записи из DTO
    public AnalysisResult updateAnalysisResult(Long recordId, AnalysisResultRequest updatedDto) {
        AnalysisResult existingRecord = analysisResultRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with ID: " + recordId));

        // Обновляем поля существующей записи

        if (updatedDto.getResearchFile() != null) {      // <--- вас конечно не должно ничего смущать
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

    public List<AnalysisResultRequest> getResultsForClient(String username) {
        // Получаем ID клиента по логину
        Client client = clientRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Client not found with login: " + username));

        // Получаем результаты анализов клиента
        List<AnalysisResult> results = analysisResultRepository.findByClientId((long) client.getId());

        // Преобразуем в DTO
        return results.stream()
                .map(analysisResult -> convertToDTO(analysisResult))
                .collect(Collectors.toList());
    }



    // Пригодится для админов и докторов
    public List<AnalysisResultRequest> getAllRecordsAsDTO() {
        return analysisResultRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // Получение записи по ID для клиента
    public List<AnalysisResultRequest> getAllByClientUsername(String username) {
        // Получаем клиента по логину
        Client client = clientRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Клиент с логином " + username + " не найден"));

        // Получаем все DiseaseHistory по clientId
        List<AnalysisResult> analysisResults = analysisResultRepository.findByClientId((long) client.getId());

        // Преобразуем в DTO (без необходимости включать сам объект Client)
        return analysisResults.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}