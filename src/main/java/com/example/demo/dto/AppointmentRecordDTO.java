package com.example.demo.dto;

import com.example.demo.entity.AppointmentRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRecordDTO {

    private Integer recordId;

    private Integer clientId;

    private Long doctorId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String serviceName;

    private Integer diseaseHistoryId;

    public AppointmentRecordDTO(AppointmentRecord record) {
        this.recordId = record.getRecordId();
        this.clientId = record.getClientId();
        this.doctorId = Long.valueOf(record.getDoctorId());
        this.appointmentDate = record.getAppointmentDate();
        this.appointmentTime = record.getAppointmentTime();
        this.serviceName = record.getServiceName();
        this.diseaseHistoryId = record.getDiseaseHistoryId();
    }
}