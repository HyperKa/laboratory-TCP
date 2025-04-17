package com.example.demo.service;

import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.dto.DiseaseHistoryDTO;
import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.Client;
import com.example.demo.entity.DiseaseHistory;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.AppointmentRecordRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DiseaseHistoryRepository;
import com.example.demo.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiseaseHistoryService {

    @Autowired
    private DiseaseHistoryRepository diseaseHistoryRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRecordRepository appointmentRecordRepository;

    // Получение всех записей истории болезни
    public Iterable<DiseaseHistory> getAllRecords() {
        return diseaseHistoryRepository.findAll();
    }

    // Получение записи по ID
    public Optional<DiseaseHistory> getRecordById(int recordId) {
        return diseaseHistoryRepository.findById(recordId);
    }

    // Создание записи истории болезни из DTO
    public DiseaseHistory createDiseaseHistoryFromDTO(DiseaseHistoryDTO dto) {
        // Находим врача по ID
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Врач с ID " + dto.getDoctorId() + " не найден"));

        // Преобразуем DTO в Entity
        DiseaseHistory history = convertToEntity(dto);
        history.setDoctor(doctor);

        // Устанавливаем клиента
        if (dto.getClientId() != null) {
            history.setClientId(dto.getClientId());
        }

        // Обрабатываем связанные записи на прием
        if (dto.getAppointmentRecordIds() != null && !dto.getAppointmentRecordIds().isEmpty()) {
            List<AppointmentRecord> appointmentRecords = dto.getAppointmentRecordIds().stream()
                    .map(id -> appointmentRecordRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Запись на прием с ID " + id + " не найдена")))
                    .collect(Collectors.toList());

            // Устанавливаем обратную связь
            appointmentRecords.forEach(record -> record.setDiseaseHistory(history));
            history.setAppointmentRecords(appointmentRecords);
        }

        // Сохраняем запись
        return diseaseHistoryRepository.save(history);
    }

    // Создание новой записи истории болезни
    public DiseaseHistory createRecord(DiseaseHistoryDTO dto) {
    // Находим врача по ID
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Врач с ID " + dto.getDoctorId() + " не найден"));

        // Преобразуем DTO в Entity
        DiseaseHistory history = convertToEntity(dto);
        history.setDoctor(doctor);

        // Устанавливаем клиента
        if (dto.getClientId() != null) {
            history.setClientId(dto.getClientId());
        }

        // Если appointmentRecordIds указаны, загружаем существующие записи
        if (dto.getAppointmentRecordIds() != null && !dto.getAppointmentRecordIds().isEmpty()) {
            List<AppointmentRecord> appointmentRecords = dto.getAppointmentRecordIds().stream()
                    .map(id -> appointmentRecordRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Запись на прием с ID " + id + " не найдена")))
                    .collect(Collectors.toList());

            // Устанавливаем обратную связь
            appointmentRecords.forEach(record -> record.setDiseaseHistory(history));
            history.setAppointmentRecords(appointmentRecords);
        } else {
            // Если appointmentRecordIds не указаны, создаем новую запись в appointment_records
            AppointmentRecord newAppointmentRecord = new AppointmentRecord();
            newAppointmentRecord.setClientById(dto.getClientId(), clientRepository);
            newAppointmentRecord.setDoctorById(dto.getDoctorId(), doctorRepository);
            newAppointmentRecord.setAppointmentDate(dto.getAppointmentDate() != null ? dto.getAppointmentDate() : LocalDate.now());
            newAppointmentRecord.setAppointmentTime(dto.getAppointmentTime() != null ? dto.getAppointmentTime() : LocalTime.now());
            newAppointmentRecord.setServiceName(dto.getServiceName() != null ? dto.getServiceName() : "Initial appointment");

            // Устанавливаем обратную связь
            newAppointmentRecord.setDiseaseHistory(history);

            // Добавляем новую запись в список appointmentRecords
            history.setAppointmentRecords(List.of(newAppointmentRecord));
        }

        // Сохраняем запись
        return diseaseHistoryRepository.save(history);
    }

    // Обновление записи истории болезни
    public DiseaseHistory updateRecord(int recordId, DiseaseHistoryDTO dto) {
        // Находим существующую запись
        DiseaseHistory history = diseaseHistoryRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Запись с ID " + recordId + " не найдена"));

        // Находим врача по ID
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Врач с ID " + dto.getDoctorId() + " не найден"));

        // Обновляем поля из DTO
        history.setDoctor(doctor);
        history.setFirstNameDoctor(dto.getFirstNameDoctor());
        history.setLastNameDoctor(dto.getLastNameDoctor());
        history.setProfession(dto.getProfession());
        history.setStartDate(dto.getStartDate());
        history.setEndDate(dto.getEndDate());
        history.setDisease(dto.getDisease());
        history.setClientId(dto.getClientId());

        // Обновляем связанные записи на прием
        if (dto.getAppointmentRecordIds() != null && !dto.getAppointmentRecordIds().isEmpty()) {
            List<AppointmentRecord> appointmentRecords = dto.getAppointmentRecordIds().stream()
                    .map(id -> appointmentRecordRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Запись на прием с ID " + id + " не найдена")))
                    .collect(Collectors.toList());

            // Устанавливаем обратную связь
            appointmentRecords.forEach(record -> record.setDiseaseHistory(history));
            history.setAppointmentRecords(appointmentRecords);
        }

        // Сохраняем изменения
        return diseaseHistoryRepository.save(history);
    }

    // Удаление записи
    public void deleteRecord(int recordId) {
        diseaseHistoryRepository.deleteById(recordId);
    }

    // Преобразование DTO -> Entity
    public DiseaseHistory convertToEntity(DiseaseHistoryDTO dto) {
        DiseaseHistory history = new DiseaseHistory();
        history.setDoctorId(dto.getDoctorId());
        history.setClientId(dto.getClientId());
        history.setFirstNameDoctor(dto.getFirstNameDoctor());
        history.setLastNameDoctor(dto.getLastNameDoctor());
        history.setProfession(dto.getProfession());
        history.setStartDate(dto.getStartDate());
        history.setEndDate(dto.getEndDate());
        history.setDisease(dto.getDisease());
        return history;
    }
    public DiseaseHistoryDTO convertToDTO(DiseaseHistory history) {
        DiseaseHistoryDTO dto = new DiseaseHistoryDTO();
        dto.setRecordId(history.getRecordId());
        dto.setDoctorId(history.getDoctorId());
        dto.setFirstNameDoctor(history.getFirstNameDoctor());
        dto.setLastNameDoctor(history.getLastNameDoctor());
        dto.setProfession(history.getProfession());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setDisease(history.getDisease());
        dto.setClientId(history.getClientId());

        // Преобразуем список AppointmentRecord в список AppointmentRecordDTO
        if (history.getAppointmentRecords() != null) {
            dto.setAppointmentRecords(history.getAppointmentRecords().stream()
                    .map(AppointmentRecordDTO::new)
                    .collect(Collectors.toList()));
        }

        return dto;
}
}