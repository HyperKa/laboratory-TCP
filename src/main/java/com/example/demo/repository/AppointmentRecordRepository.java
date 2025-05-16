package com.example.demo.repository;

import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.AppointmentRecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRecordRepository extends JpaRepository<AppointmentRecord, Integer> {
    List<AppointmentRecord> findByClient_Id(Long clientId);
}
