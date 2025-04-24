package com.example.demo.task;

import com.example.demo.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BlacklistCleanupTask {

    @Autowired
    private BlacklistService blacklistService;

    @Scheduled(cron = "0 0 * * * ?") // Каждый час
    public void cleanupExpiredTokens() {
        blacklistService.cleanupExpiredTokens(LocalDateTime.now());
    }
}