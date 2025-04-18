package com.example.demo.service;

import com.example.demo.entity.BlacklistedToken; // Импортируем класс BlacklistedToken
import com.example.demo.repository.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class BlacklistedTokenService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Добавляет токен в черный список
     */
    public void addToBlacklist(String token) {
        // Создаем объект BlacklistedToken
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token); // Устанавливаем токен
        blacklistedToken.setRevokedAt(LocalDateTime.now()); // Устанавливаем время отзыва

        // Сохраняем объект в базу данных
        blacklistedTokenRepository.save(blacklistedToken);
    }

    /**
     * Проверяет, находится ли токен в черном списке
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}