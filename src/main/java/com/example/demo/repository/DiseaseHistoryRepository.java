package com.example.demo.repository;

import com.example.demo.entity.DiseaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseHistoryRepository extends JpaRepository<DiseaseHistory, Integer> {

}
