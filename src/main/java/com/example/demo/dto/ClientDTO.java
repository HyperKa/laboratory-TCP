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

    private String login;
    private String password;
    private Role role;

    public ClientDTO(Client client) {
        this.id = (long) client.getId();
        this.age = client.getAge();
        this.gender = client.getGender();
        this.lastName = client.getLastName();
        this.firstName = client.getFirstName();
        this.address = client.getAddress();
        this.passport = client.getPassport();
        this.login = client.getLogin();
        this.role = client.getRole();
    }
}