package com.example.demo.repository;

import com.example.demo.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
}
