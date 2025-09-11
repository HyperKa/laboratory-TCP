package com.example.demo.dto;

import com.example.demo.entity.AppointmentRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRecordDTO {

    private Long recordId;

    private Long clientId;

    private Long doctorId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String serviceName;

    private Long diseaseHistoryId;

    // Конструктор для преобразования из Entity в DTO
    public AppointmentRecordDTO(AppointmentRecord record) {
        this.recordId = record.getRecordId();
        //this.clientId = record.getClient() != null ? record.getClient().getId() : null;
        //this.doctorId = Long.valueOf(record.getDoctor() != null ? record.getDoctor().getId() : null);
        this.clientId = record.getClientId(); // Используем геттер для ID клиента
        this.doctorId = record.getDoctorId();
        this.appointmentDate = record.getAppointmentDate();
        this.appointmentTime = record.getAppointmentTime();
        this.serviceName = record.getServiceName();
        //this.diseaseHistoryId = record.getDiseaseHistory() != null ? record.getDiseaseHistory().getRecordId() : null;
        this.diseaseHistoryId = record.getDiseaseHistoryId();
    }
}