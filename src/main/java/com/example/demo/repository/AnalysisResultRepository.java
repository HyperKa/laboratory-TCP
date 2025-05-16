package com.example.demo.repository;

import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.AppointmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AppointmentRecord> findByClient_Login(String login);
}
