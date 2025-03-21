package com.example.demo.service;

/*
import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
*/

import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.Client;
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
    private ClientRepository clientRepository;

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
}