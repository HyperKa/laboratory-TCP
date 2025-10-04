package com.example.demo.dto;

import com.example.demo.entity.Client;
import com.example.demo.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private Long id;
    private int age;
    private String gender;
    private String lastName;
    private String firstName;
    private String address;
    private String passport;

    // Новые поля
    private String login;
    //@JsonIgnore
    private String password;
    private Role role; // или Role (если используете Enum)

    // Конструктор для преобразования из Entity в DTO
    public ClientDTO(Client client) {
        this.id = client.getId();
        this.age = client.getAge();
        this.gender = client.getGender();
        this.lastName = client.getLastName();
        this.firstName = client.getFirstName();
        this.address = client.getAddress();
        this.passport = client.getPassport();
        this.login = client.getLogin();
        //this.password = client.getPassword();
        this.role = client.getRole();
    }
}