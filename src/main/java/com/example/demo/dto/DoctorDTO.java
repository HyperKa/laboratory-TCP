package com.example.demo.dto;

import com.example.demo.entity.Doctor;
import com.example.demo.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DoctorDTO {
    private Long id;
    private String lastName;
    private String firstName;
    private String specialization;
    private String experience;
    private String login;
    private String password;
    private Role role;

    public DoctorDTO(Doctor doctor) {
        this.id = doctor.getId();
        this.lastName = doctor.getLastName();
        this.firstName = doctor.getFirstName();
        this.specialization = doctor.getSpecialization();
        this.experience = doctor.getExperience();
        this.login = doctor.getLogin();
        this.role = doctor.getRole();
    }
}
