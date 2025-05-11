package com.example.demo.repository;

import com.example.demo.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {
    Optional<Blacklist> findByToken(String token);

    @Modifying
    @Query("DELETE FROM Blacklist b WHERE b.expiryDate < :now")
    void deleteByExpiryDateBefore(LocalDateTime now);
}