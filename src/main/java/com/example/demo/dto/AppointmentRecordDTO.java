package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRecordDTO {

    private Integer clientId; // ID клиента
    private Long doctorId; // ID врача
    private LocalDate appointmentDate; // Дата записи
    private LocalTime appointmentTime; // Время записи
    private String serviceName; // Название услуги
    private Long diseaseHistoryId; // ID истории болезни (опционально)
}