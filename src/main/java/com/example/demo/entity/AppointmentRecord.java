package com.example.demo.entity;

import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DiseaseHistoryRepository;
import com.example.demo.repository.DoctorRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointment_records")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AppointmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    private Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference // Дочерняя сторона, игнорируется при сериализации
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    // Связь 1:1 с таблицей "История болезни"
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    //@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_history_id", referencedColumnName = "record_id")
    @JsonIgnore // Игнорируем это поле при сериализации
    private DiseaseHistory diseaseHistory;

    // Метод для установки клиента по ID
    public void setClientById(Integer clientId, ClientRepository clientRepository) {
        if (clientId != null) {
            this.client = clientRepository.findById(Long.valueOf(clientId))
                    .orElseThrow(() -> new RuntimeException("Клиент с ID " + clientId + " не найден"));
        } else {
            this.client = null;
        }
    }

    // Метод для установки врача по ID
    public void setDoctorById(Long doctorId, DoctorRepository doctorRepository) {
        if (doctorId != null) {
            this.doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Врач с ID " + doctorId + " не найден"));
        } else {
            this.doctor = null;
        }
    }

    // Метод для установки истории болезни по ID
    public void setDiseaseHistoryById(Long diseaseHistoryId, DiseaseHistoryRepository diseaseHistoryRepository) {
        if (diseaseHistoryId != null) {
            this.diseaseHistory = diseaseHistoryRepository.findById(Math.toIntExact(diseaseHistoryId))
                    .orElseThrow(() -> new RuntimeException("История болезни с ID " + diseaseHistoryId + " не найдена"));
        } else {
            this.diseaseHistory = null;
        }
    }

    public Integer getClientId() {
        return client != null ? Math.toIntExact(client.getId()) : null;
    }

    public Integer getDoctorId() {
        return doctor != null ? doctor.getId() : null;
    }

    public Integer getDiseaseHistoryId() {
        return diseaseHistory != null ? diseaseHistory.getRecordId() : null;
    }
}