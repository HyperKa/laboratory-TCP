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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

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

    @Autowired
    private AppointmentRecordService appointmentRecordService;

    // Получение всех записей истории болезни
    public Iterable<DiseaseHistory> getAllRecords() {
        return diseaseHistoryRepository.findAll();
    }

    // Получение записи по ID
    public Optional<DiseaseHistory> getRecordById(int recordId) {
        return diseaseHistoryRepository.findById(recordId);
    }

    /*
    public DiseaseHistory createRecord(DiseaseHistoryDTO dto) {
        // Находим врача по ID
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Врач с ID " + dto.getDoctorId() + " не найден"));

        // Преобразуем DTO в Entity
        DiseaseHistory history = convertToEntity(dto);
        history.setDoctor(doctor);

        // Сохраняем запись
        return diseaseHistoryRepository.save(history);
    }
    */
    private static final Logger logger = LoggerFactory.getLogger(DiseaseHistoryService.class);

    public DiseaseHistory createRecord(DiseaseHistoryDTO dto) {

        logger.info("Creating DiseaseHistory record with doctorId: {}", dto.getDoctorId());

        // Находим врача по ID
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Врач с ID " + dto.getDoctorId() + " не найден"));

        // Преобразуем DTO в Entity
        DiseaseHistory history = convertToEntity(dto);
        history.setDoctor(doctor);

        // Устанавливаем клиента
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(Long.valueOf(dto.getClientId()))
                    .orElseThrow(() -> new RuntimeException("Клиент с ID " + dto.getClientId() + " не найден"));
            //history.setClient(client);
            history.setClientId(client.getId());
        }

        // Если appointmentRecordId указан, загружаем существующую запись
        if (dto.getAppointmentRecordId() != null) {
            AppointmentRecordDTO appointmentRecordDTO = appointmentRecordService.getRecordByIdAsDTO(dto.getAppointmentRecordId())
                    .orElseThrow(() -> new RuntimeException("Запись на прием с ID " + dto.getAppointmentRecordId() + " не найдена"));

            // Преобразуем DTO обратно в Entity
            AppointmentRecord appointmentRecord = appointmentRecordService.convertToEntity(appointmentRecordDTO);
            history.setAppointmentRecord(appointmentRecord);
            appointmentRecord.setDiseaseHistory(history); // Устанавливаем обратную связь
        } else {
            // Если appointmentRecordId не указан, создаем новую запись в appointment_records
            AppointmentRecordDTO appointmentRecordDTO = new AppointmentRecordDTO();
            appointmentRecordDTO.setClientId(dto.getClientId());
            appointmentRecordDTO.setDoctorId(dto.getDoctorId());
            // appointmentRecordDTO.setAppointmentDate(LocalDateTime.now().toLocalDate());
            // appointmentRecordDTO.setAppointmentTime(LocalDateTime.now().toLocalTime());
            // appointmentRecordDTO.setServiceName("Initial appointment");
            appointmentRecordDTO.setAppointmentDate(dto.getAppointmentDate() != null ? dto.getAppointmentDate() : LocalDate.now());
            appointmentRecordDTO.setAppointmentTime(dto.getAppointmentTime() != null ? dto.getAppointmentTime() : LocalTime.now());
            appointmentRecordDTO.setServiceName(dto.getServiceName() != null ? dto.getServiceName() : "Initial appointment");

            AppointmentRecord newAppointmentRecord = appointmentRecordService.createRecordFromDTO(appointmentRecordDTO);
            history.setAppointmentRecord(newAppointmentRecord);
            newAppointmentRecord.setDiseaseHistory(history); // Устанавливаем обратную связь
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
        history.setAppointmentRecordId(dto.getAppointmentRecordId());

        // Сохраняем изменения
        return diseaseHistoryRepository.save(history);
    }

    // Удаление записи
    public void deleteRecord(int recordId) {
        diseaseHistoryRepository.deleteById(recordId);
    }

    // Преобразование Entity -> DTO
    /*
    public DiseaseHistoryDTO convertToDTO(DiseaseHistory history) {
        DiseaseHistoryDTO dto = new DiseaseHistoryDTO();
        //dto.setRecordId(history.getRecordId());
        dto.setDoctorId(Long.valueOf(history.getDoctorId()));

        if (history.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));
        } else {
            throw new RuntimeException("doctorId cannot be null");
        }
        dto.setFirstNameDoctor(history.getFirstNameDoctor());
        dto.setLastNameDoctor(history.getLastNameDoctor());
        dto.setProfession(history.getProfession());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setDisease(history.getDisease());
        dto.setClientId(history.getClientId());
        dto.setAppointmentRecordId(history.getAppointmentRecordId());
        return dto;
    }
    */

    public DiseaseHistoryDTO convertToDTO(DiseaseHistory history) {
        return new DiseaseHistoryDTO(history); // Используем конструктор с параметрами
    }

    public DiseaseHistory convertToEntity(DiseaseHistoryDTO dto) {
        DiseaseHistory diseaseHistory = new DiseaseHistory();

        diseaseHistory.setDoctorId(dto.getDoctorId());
        //diseaseHistory.setClientId(dto.getClientId());
        diseaseHistory.setAppointmentRecordId(dto.getAppointmentRecordId());
        if (dto.getClientId() != null) {
            diseaseHistory.setClientId(dto.getClientId());
        }
        else {
            System.out.println("Запись не вывелась, типо Id = 0");
        }

        // Устанавливаем остальные поля
        diseaseHistory.setFirstNameDoctor(dto.getFirstNameDoctor());
        diseaseHistory.setLastNameDoctor(dto.getLastNameDoctor());
        diseaseHistory.setProfession(dto.getProfession());
        diseaseHistory.setStartDate(dto.getStartDate());
        diseaseHistory.setEndDate(dto.getEndDate());
        diseaseHistory.setDisease(dto.getDisease());

        return diseaseHistory;
    }
}