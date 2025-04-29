package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;

@Entity
@Table(name = "clients")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Client implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "address")
    private String address;

    @Column(name = "passport", nullable = false)
    private String passport;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

/*
    // Связь 1:1 с таблицей "История болезни"
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_history_id", referencedColumnName = "record_id")
    private DiseaseHistory diseaseHistory;
*/

    // Связь 1:N с таблицей "Результаты анализов"
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisResult> analysisResults = new ArrayList<>();

    // Связь 1:N с таблицей "Список записей"
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AppointmentRecord> appointmentRecords = new ArrayList<>();


    public Client() {}

    public Client(int age, String gender, String lastName, String firstName, String address, String passport) {
        this.age = age;
        this.gender = gender;
        this.lastName = lastName;
        this.firstName = firstName;
        this.address = address;
        this.passport = passport;
    }

    public Client(int age, String gender, String lastName, String firstName, String passport) {
        this.age = age;
        this.gender = gender;
        this.lastName = lastName;
        this.firstName = firstName;
        this.passport = passport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", address='" + address + '\'' +
                ", passport='" + passport + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Учетная запись всегда активна
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Учетная запись не заблокирована
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Срок действия пароля не истек
    }

    @Override
    public boolean isEnabled() {
        return true; // Учетная запись включена
    }

}
