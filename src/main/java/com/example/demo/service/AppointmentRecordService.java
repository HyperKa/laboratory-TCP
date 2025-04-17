package com.example.demo.service;

import com.example.demo.dto.AppointmentRecordDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentRecordService {

    @Autowired
    private AppointmentRecordRepository appointmentRecordRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DiseaseHistoryRepository diseaseHistoryRepository;

    // Преобразование Entity -> DTO
    public AppointmentRecordDTO convertToDTO(AppointmentRecord record) {
        return new AppointmentRecordDTO(record);
    }

    // Преобразование DTO -> Entity
    public AppointmentRecord convertToEntity(AppointmentRecordDTO dto) {
        AppointmentRecord record = new AppointmentRecord();

        // Загружаем клиента по clientId
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(Long.valueOf(dto.getClientId()))
                    .orElseThrow(() -> new RuntimeException("Client not found with ID: " + dto.getClientId()));
            record.setClient(client);
        } else {
            throw new RuntimeException("clientId cannot be null");
        }

        // Загружаем врача по doctorId
        if (dto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));
            record.setDoctor(doctor);
        } else {
            throw new RuntimeException("doctorId cannot be null");
        }

        // Устанавливаем остальные поля
        record.setAppointmentDate(dto.getAppointmentDate());
        record.setAppointmentTime(dto.getAppointmentTime());
        record.setServiceName(dto.getServiceName());

        // Загружаем историю болезни по diseaseHistoryId
        if (dto.getDiseaseHistoryId() != null) {
            DiseaseHistory diseaseHistory = diseaseHistoryRepository.findById(dto.getDiseaseHistoryId())
                    .orElseThrow(() -> new RuntimeException("DiseaseHistory not found with ID: " + dto.getDiseaseHistoryId()));
            record.setDiseaseHistory(diseaseHistory);

            // Добавляем запись в список appointmentRecords истории болезни
            diseaseHistory.getAppointmentRecords().add(record);
        }

        return record;
    }

    // Получение всех записей в виде DTO
    public List<AppointmentRecordDTO> getAllRecordsAsDTO() {
        return appointmentRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение записи по ID в виде DTO
    public Optional<AppointmentRecordDTO> getRecordByIdAsDTO(Integer recordId) {
        return appointmentRecordRepository.findById(recordId).map(this::convertToDTO);
    }

    // Получение записи по ID
    public Optional<AppointmentRecord> getRecordById(Integer recordId) {
        return appointmentRecordRepository.findById(recordId);
    }

    // Создание записи из DTO
    public AppointmentRecord createRecordFromDTO(AppointmentRecordDTO dto) {
        AppointmentRecord record = convertToEntity(dto);
        return appointmentRecordRepository.save(record);
    }


    // Обновление записи из DTO
    public AppointmentRecord updateRecordFromDTO(Integer recordId, AppointmentRecordDTO updatedDto) {
        AppointmentRecord existingRecord = appointmentRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with ID: " + recordId));

        // Обновляем поля существующей записи
        if (updatedDto.getClientId() != null) {
            existingRecord.setClientById(updatedDto.getClientId(), clientRepository);
        }
        if (updatedDto.getDoctorId() != null) {
            existingRecord.setDoctorById(updatedDto.getDoctorId(), doctorRepository);
        }
        if (updatedDto.getAppointmentDate() != null) {
            existingRecord.setAppointmentDate(updatedDto.getAppointmentDate());
        }
        if (updatedDto.getAppointmentTime() != null) {
            existingRecord.setAppointmentTime(updatedDto.getAppointmentTime());
        }
        if (updatedDto.getServiceName() != null) {
            existingRecord.setServiceName(updatedDto.getServiceName());
        }
        if (updatedDto.getDiseaseHistoryId() != null) {
            DiseaseHistory diseaseHistory = diseaseHistoryRepository.findById(updatedDto.getDiseaseHistoryId())
                    .orElseThrow(() -> new RuntimeException("DiseaseHistory not found with ID: " + updatedDto.getDiseaseHistoryId()));

            // Обновляем связь с историей болезни
            existingRecord.setDiseaseHistory(diseaseHistory);
            diseaseHistory.getAppointmentRecords().add(existingRecord);
        }

        return appointmentRecordRepository.save(existingRecord);
    }

    // Удаление записи
    public void deleteRecord(Integer recordId) {
        AppointmentRecord record = appointmentRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with ID: " + recordId));

        // Удаляем запись из списка appointmentRecords истории болезни
        if (record.getDiseaseHistory() != null) {
            DiseaseHistory diseaseHistory = record.getDiseaseHistory();
            diseaseHistory.getAppointmentRecords().remove(record); // Удаляем запись из списка
            record.setDiseaseHistory(null); // Обнуляем ссылку на историю болезни
            diseaseHistoryRepository.save(diseaseHistory); // Сохраняем изменения
        }

        appointmentRecordRepository.deleteById(recordId);
    }

    // Получение всех записей на прием для конкретной истории болезни
    public List<AppointmentRecordDTO> getRecordsByDiseaseHistoryId(Integer diseaseHistoryId) {
        DiseaseHistory diseaseHistory = diseaseHistoryRepository.findById(diseaseHistoryId)
                .orElseThrow(() -> new RuntimeException("DiseaseHistory not found with ID: " + diseaseHistoryId));

        return diseaseHistory.getAppointmentRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}