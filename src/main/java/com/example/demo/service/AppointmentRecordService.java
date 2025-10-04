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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private DoctorService doctorService;

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
            Client client = clientRepository.findById(dto.getClientId())
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
    public Optional<AppointmentRecordDTO> getRecordByIdAsDTO(Long recordId) {
        return appointmentRecordRepository.findById(recordId).map(this::convertToDTO);
    }

    // Получение записи по ID
    public Optional<AppointmentRecord> getRecordById(Long recordId) {
        return appointmentRecordRepository.findById(recordId);
    }

    //закомментированно намеренно, чтобы система могла сама создавать disease_history запись без предварительного участия врача
    /*
    // Создание записи из DTO
    public AppointmentRecord createRecordFromDTO(AppointmentRecordDTO dto) {
        AppointmentRecord record = convertToEntity(dto);
        return appointmentRecordRepository.save(record);
    }
     */

    public AppointmentRecord createRecordFromDTO(AppointmentRecordDTO dto, String username) {
        // Найти клиента по логину
        Client client = clientRepository.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        // Найти врача по ID
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DiseaseHistory diseaseHistory;

        // Если в DTO указан ID истории болезни — используем его
        if (dto.getDiseaseHistoryId() != null) {
            diseaseHistory = diseaseHistoryRepository.findById(dto.getDiseaseHistoryId())
                .orElseThrow(() -> new RuntimeException("DiseaseHistory not found"));
        } else {
            // Поиск активной истории болезни по клиенту и текущему диагнозу
            diseaseHistory = diseaseHistoryRepository.findFirstByClientIdAndDisease(client.getId(), dto.getServiceName())
                .orElseGet(() -> {
                    DiseaseHistory dh = new DiseaseHistory();
                    dh.setClientId(client.getId());
                    dh.setDoctor(doctor);
                    dh.setDoctorId(doctor.getId());
                    dh.setFirstNameDoctor(doctor.getFirstName());
                    dh.setLastNameDoctor(doctor.getLastName());
                    dh.setProfession(doctor.getSpecialization());
                    dh.setStartDate(LocalDateTime.now());
                    dh.setEndDate(LocalDateTime.now());
                    dh.setDisease(dto.getServiceName()); // используем название услуги как временный диагноз
                    return diseaseHistoryRepository.save(dh);
                });
        }

        // Создание новой записи на приём
        AppointmentRecord record = new AppointmentRecord();
        record.setClient(client);
        record.setDoctor(doctor);
        record.setAppointmentDate(dto.getAppointmentDate());
        record.setAppointmentTime(dto.getAppointmentTime());
        record.setServiceName(dto.getServiceName());
        record.setDiseaseHistory(diseaseHistory);

        // Сохраняем и добавляем в историю болезни
        AppointmentRecord saved = appointmentRecordRepository.save(record);
        // Защита от null — инициализируем список, если он ещё не создан
        if (diseaseHistory.getAppointmentRecords() == null) {
            diseaseHistory.setAppointmentRecords(new ArrayList<>());
        }

        diseaseHistory.getAppointmentRecords().add(saved);

        return saved;
    }


    // Обновление записи из DTO
    public AppointmentRecord updateRecordFromDTO(Long recordId, AppointmentRecordDTO updatedDto) {
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
    public void deleteRecord(Long recordId) {
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
    public List<AppointmentRecordDTO> getRecordsByDiseaseHistoryId(Long diseaseHistoryId) {
        DiseaseHistory diseaseHistory = diseaseHistoryRepository.findById(diseaseHistoryId)
                .orElseThrow(() -> new RuntimeException("DiseaseHistory not found with ID: " + diseaseHistoryId));

        return diseaseHistory.getAppointmentRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentRecordDTO> getRecordsByClientUsername(String username) {
        Client client = clientRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        return appointmentRecordRepository.findByClient(client)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<AppointmentRecordDTO> getAppointmentsForUser(String username, String role) {
        // Получаем ID клиента по логину
        if ("ROLE_CLIENT".equals(role)) {
            Client client = clientRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Client not found with login: " + username));
            List<AppointmentRecord> records = appointmentRecordRepository.findByClient(client);
            return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        } else if ("ROLE_DOCTOR".equals(role)) {
            Doctor doctor = doctorService.findByLogin(username);
            return appointmentRecordRepository.findByDoctor(doctor).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else if ("ROLE_ADMIN".equals(role)) {
            return appointmentRecordRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Неподдерживаемая роль: " + role);
    }
}