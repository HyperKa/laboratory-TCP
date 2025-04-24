package com.example.demo.service;

import com.example.demo.entity.Blacklist;
import com.example.demo.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlacklistService {

    @Autowired
    private BlacklistRepository blacklistRepository;

    // Добавление токена в черный список
    public void addToBlacklist(String token, LocalDateTime expiryDate) {
        Blacklist blacklistEntry = new Blacklist();
        blacklistEntry.setToken(token);
        blacklistEntry.setExpiryDate(expiryDate);
        blacklistRepository.save(blacklistEntry);
    }

    // Проверка, находится ли токен в черном списке
    public boolean isTokenBlacklisted(String token) {
        return blacklistRepository.findByToken(token).isPresent();
    }

    public void cleanupExpiredTokens(LocalDateTime now) {
        blacklistRepository.deleteByExpiryDateBefore(now);
    }
}
