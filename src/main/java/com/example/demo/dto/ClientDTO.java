package com.example.demo.dto;

import com.example.demo.entity.Client;
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

    // Конструктор для преобразования из Entity в DTO
    public ClientDTO(Client client) {
        this.id = (long) client.getId();
        this.age = client.getAge();
        this.gender = client.getGender();
        this.lastName = client.getLastName();
        this.firstName = client.getFirstName();
        this.address = client.getAddress();
        this.passport = client.getPassport();
    }
}