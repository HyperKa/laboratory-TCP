package com.example.demo.repository;

import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.AppointmentRecordId;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRecordRepository extends JpaRepository<AppointmentRecord, Integer> {
    List<AppointmentRecord> findByClient(Client client);
    //List<AppointmentRecord> findByClientId(Long clientId);
    //List<AppointmentRecord> findByClientId(Integer clientId);

    //List<AppointmentRecord> findByDoctorId(Long doctorId);

    List<AppointmentRecord> findByDoctor(Doctor doctor);
}
