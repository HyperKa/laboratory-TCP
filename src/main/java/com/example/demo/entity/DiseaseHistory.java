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
@Table(name="disease_history")
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
    @JsonIgnoreProperties("diseaseHistories")
    //@JsonIgnoreProperties("diseaseHistories")
    @JsonManagedReference // Родительская сторона
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

    // Поля для хранения идентификаторов
    @JsonProperty("clientId")
    @Column(name = "client_id")
    private Integer clientId;

    @JsonProperty("doctorId")
    @Column(name = "doctor_id", insertable = false, updatable = false)
    private Long doctorId;

    @JsonProperty("appointmentRecordId")
    @Column(name = "appointment_record_id", insertable = false, updatable = false)
    private Integer appointmentRecordId;

/*
    // Связь 1:1 с таблицей "Клиенты"
    @JsonIgnoreProperties("appointmentRecords")
    @OneToOne(mappedBy = "diseaseHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Client client;
*/


    // Связь 1:1 с таблицей "Список записей"
    @JsonIgnoreProperties("appointmentRecords")
    @OneToOne(mappedBy = "diseaseHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference // Дочерняя сторона
    private AppointmentRecord appointmentRecord;


/*
    @OneToMany(mappedBy = "diseaseHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AppointmentRecord> appointmentRecord;
*/

    // Геттеры для связей
    public Integer getClientId() {
        return clientId;
    }

    public Integer getAppointmentRecordId() {
        return appointmentRecord != null ? appointmentRecord.getRecordId() : null;
    }

    public Integer getDoctorId() {
        //return doctor != null ? doctor.getId() : null;
        return doctor.getId();
    }

    // Метод для установки клиента по ID
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    // Метод для установки врача по ID
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    // Сеттер для appointmentRecordId
    public void setAppointmentRecordId(Integer appointmentRecordId) {
        this.appointmentRecordId = appointmentRecordId;
    }

}
