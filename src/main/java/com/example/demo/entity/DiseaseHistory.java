package com.example.demo.entity;

import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DiseaseHistoryRepository;
import com.example.demo.repository.DoctorRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Table(name = "disease_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @Column(name = "start_date")
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")
    private LocalDateTime endDate = LocalDateTime.now();

    @Column(name = "disease", nullable = false)
    private String disease;

    @JsonProperty("clientId")
    @Column(name = "client_id")
    private Integer clientId;

    @JsonProperty("doctorId")
    @Column(name = "doctor_id", insertable = false, updatable = false)
    private Long doctorId;

    // Связь 1:N с таблицей "Список записей"
    @OneToMany(mappedBy = "diseaseHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY,  orphanRemoval = false)
    @JsonIgnoreProperties("diseaseHistory")
    private List<AppointmentRecord> appointmentRecords;

    // Геттеры для связей
    public Integer getClientId() {
        return clientId;
    }

    public List<AppointmentRecord> getAppointmentRecords() {
        return appointmentRecords;
    }
}
