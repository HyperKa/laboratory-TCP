package com.example.demo.dto;

import com.example.demo.entity.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DoctorDTO {
    private int id;
    private String lastName;
    private String firstName;
    private String specialization;
    private String experience;
    private String login;
    private String password;

    // Конструктор для преобразования из Entity в DTO
    public DoctorDTO(Doctor doctor) {
        this.id = (int) doctor.getId();
        this.lastName = doctor.getLastName();
        this.firstName = doctor.getFirstName();
        this.specialization = doctor.getSpecialization();
        this.experience = doctor.getExperience();
        this.login = doctor.getLogin();
    }
}
