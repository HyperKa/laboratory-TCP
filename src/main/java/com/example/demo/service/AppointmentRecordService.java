package com.example.demo.service;

import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.AppointmentRecordId;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.DiseaseHistory;
import com.example.demo.repository.AppointmentRecordRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.DiseaseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentRecordService {

    @Autowired
    private AppointmentRecordRepository appointmentRecordRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DiseaseHistoryRepository diseaseHistoryRepository;

    // Получение всех записей
    public List<AppointmentRecord> getAllRecords() {
        return appointmentRecordRepository.findAll();
    }

    // Получение записи по составному ключу
    public Optional<AppointmentRecord> getRecordById(Long clientId, Long recordId) {
        AppointmentRecordId id = new AppointmentRecordId(clientId, recordId);
        return appointmentRecordRepository.findById(id);
    }

    // Создание новой записи
    public AppointmentRecord createRecord(Long clientId, Long doctorId, LocalDate appointmentDate,
                                          LocalTime appointmentTime, String serviceName, Long diseaseHistoryId) {

        // Находим клиента
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент с ID " + clientId + " не найден"));

        // Находим врача
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Врач с ID " + doctorId + " не найден"));

        // Находим историю болезни (если есть)
        DiseaseHistory diseaseHistory = null;
        if (diseaseHistoryId != null) {
            diseaseHistory = diseaseHistoryRepository.findById(Math.toIntExact(diseaseHistoryId))
                    .orElseThrow(() -> new RuntimeException("История болезни с ID " + diseaseHistoryId + " не найдена"));
        }

        // Создаем объект записи
        AppointmentRecord record = new AppointmentRecord();
        record.setId(new AppointmentRecordId(clientId, generateRecordId())); // Генерация recordId
        record.setClient(client);
        record.setDoctor(doctor);
        record.setAppointmentDate(appointmentDate);
        record.setAppointmentTime(appointmentTime);
        record.setServiceName(serviceName);
        record.setDiseaseHistory(diseaseHistory);

        // Сохраняем запись
        return appointmentRecordRepository.save(record);
    }

    // Удаление записи
    public void deleteRecord(Long clientId, Long recordId) {
        AppointmentRecordId id = new AppointmentRecordId(clientId, recordId);
        appointmentRecordRepository.deleteById(id);
    }

    // Генерация уникального значения для recordId
    private Long generateRecordId() {
        // Пример простой генерации (можно заменить на более сложную логику)
        return Math.round(Math.random() * 100000L);
    }
}