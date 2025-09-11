package com.example.demo.repository;

import com.example.demo.entity.DiseaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiseaseHistoryRepository extends JpaRepository<DiseaseHistory, Long> {
    Optional<DiseaseHistory> findFirstByClientIdAndDisease(Long clientId, String disease);
    List<DiseaseHistory> findByClientId(Long clientId);
    List<DiseaseHistory> findByDoctorId(Long doctorId);

}
