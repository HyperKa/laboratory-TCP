package com.example.demo.dto;

import com.example.demo.entity.DiseaseHistory;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor

public class DiseaseHistoryDTO {
    private int recordId;
    private Long doctorId;
    private String firstNameDoctor;
    private String lastNameDoctor;
    private String profession;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String disease;

    private Integer clientId; // Идентификатор клиента
    private Integer appointmentRecordId; // Идентификатор записи на прием

    // Поля для appointmentRecord
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String serviceName;

    // Getters and Setters для recordId

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getAppointmentRecordId() {
        return appointmentRecordId;
    }

    public void setAppointmentRecordId(Integer appointmentRecordId) {
        this.appointmentRecordId = appointmentRecordId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    // Getters and Setters
    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getFirstNameDoctor() {
        return firstNameDoctor;
    }

    public void setFirstNameDoctor(String firstNameDoctor) {
        this.firstNameDoctor = firstNameDoctor;
    }

    public String getLastNameDoctor() {
        return lastNameDoctor;
    }

    public void setLastNameDoctor(String lastNameDoctor) {
        this.lastNameDoctor = lastNameDoctor;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public DiseaseHistoryDTO(DiseaseHistory history) {
        this.recordId = history.getRecordId();
        this.doctorId = Long.valueOf(history.getDoctorId()); // Используем геттер для ID врача
        this.firstNameDoctor = history.getFirstNameDoctor();
        this.lastNameDoctor = history.getLastNameDoctor();
        this.profession = history.getProfession();
        this.startDate = history.getStartDate();
        this.endDate = history.getEndDate();
        this.disease = history.getDisease();
        this.clientId = history.getClientId(); // Используем геттер для ID клиента
        this.appointmentRecordId = history.getAppointmentRecordId(); // Используем геттер для ID записи на прием
        // Если appointmentRecord существует, заполняем его поля
        if (history.getAppointmentRecord() != null) {
            this.appointmentDate = history.getAppointmentRecord().getAppointmentDate();
            this.appointmentTime = history.getAppointmentRecord().getAppointmentTime();
            this.serviceName = history.getAppointmentRecord().getServiceName();
        }
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
