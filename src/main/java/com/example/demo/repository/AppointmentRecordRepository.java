package com.example.demo.repository;

import com.example.demo.entity.AppointmentRecord;
import com.example.demo.entity.AppointmentRecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AppointmentRecordRepository extends JpaRepository<AppointmentRecord, Integer> {
}
