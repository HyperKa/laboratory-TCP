package com.example.demo.dto;

import java.time.LocalDateTime;

public class DiseaseHistoryDTO {
    private int recordId;
    private int doctorId;
    private String firstNameDoctor;
    private String lastNameDoctor;
    private String profession;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String disease;

    // Getters and Setters для recordId
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    // Getters and Setters
    public Long getDoctorId() {
        return (long) doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = Math.toIntExact(doctorId);
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
}
