package com.example.demo.dto;

import com.example.demo.entity.DiseaseHistory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.CriteriaBuilder;
import com.example.demo.entity.AppointmentRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor

public class DiseaseHistoryDTO {

    private int recordId;
    private Long doctorId;
    private String firstNameDoctor;
    private String lastNameDoctor;
    private String profession;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String disease;
    private Integer clientId;

    // Добавляем новые поля
    @JsonIgnore
    private LocalDate appointmentDate;
    @JsonIgnore
    private LocalTime appointmentTime;
    @JsonIgnore
    private String serviceName;

    // Поле для хранения списка ID записей на прием
    @JsonIgnore
    private List<Integer> appointmentRecordIds;
    // Поле для хранения полных объектов AppointmentRecord
    private List<AppointmentRecordDTO> appointmentRecords;

    // Конструкторы, геттеры и сеттеры
    public DiseaseHistoryDTO() {}

    public DiseaseHistoryDTO(DiseaseHistory history) {
        this.recordId = history.getRecordId();
        this.doctorId = history.getDoctorId();
        this.firstNameDoctor = history.getFirstNameDoctor();
        this.lastNameDoctor = history.getLastNameDoctor();
        this.profession = history.getProfession();
        this.startDate = history.getStartDate();
        this.endDate = history.getEndDate();
        this.disease = history.getDisease();
        this.clientId = history.getClientId();
        this.appointmentRecordIds = history.getAppointmentRecords().stream()
                .map(AppointmentRecord::getRecordId)
                .collect(Collectors.toList());
    }

    // Геттеры и сеттеры
    public List<Integer> getAppointmentRecordIds() {
        return appointmentRecordIds;
    }

    public void setAppointmentRecordIds(List<Integer> appointmentRecordIds) {
        this.appointmentRecordIds = appointmentRecordIds;
    }
}
