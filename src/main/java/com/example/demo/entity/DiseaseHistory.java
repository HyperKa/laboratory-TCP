package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="disease_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DiseaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private int recordId;

    // Связь с таблицей "Врачи"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "first_name_doctor", nullable = false)
    private String firstNameDoctor;

    @Column(name = "last_name_doctor", nullable = false)
    private String lastNameDoctor;

    @Column(name = "profession", nullable = false)
    private String profession;

    @Column(name = "start_date")  // Подумать о фиксации времени
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")  // Думай
    private LocalDateTime endDate = LocalDateTime.now();

    @Column(name = "disease", nullable = false)
    private String disease;

    // Связь 1:1 с таблицей "Клиенты"
    @OneToOne(mappedBy = "diseaseHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Client client;

    // Связь 1:1 с таблицей "Список записей"
    @OneToOne(mappedBy = "diseaseHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AppointmentRecord appointmentRecord;
}
