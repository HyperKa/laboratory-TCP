// src/main/java/com/example/demo/config/ApiSecurityConfig.java
package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtTokenService;
import com.example.demo.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(1) // Этот конфиг применяется ПЕРВЫМ
public class ApiSecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
                                              JwtTokenService jwtTokenService,
                                              UserDetailsService userDetailsService,
                                              BlacklistService blacklistService) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtTokenService,
                userDetailsService,
                blacklistService
        );

        http
                // --- Применять этот конфиг ТОЛЬКО для /api/** ---
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- Правила для публичных API эндпоинтов ---
                        .requestMatchers("/api/auth/login", "/api/auth/register/client").permitAll()
                        .requestMatchers("/api/auth/register/doctor").hasRole("ADMIN")
                        .requestMatchers("/api/auth/register/admin").permitAll() // Временно

                        // --- Правила авторизации для API эндпоинтов ---
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/analysis-results/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointment-records/{recordId}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/disease-history/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")

                        // Ограничения для докторов на изменение своей таблицы
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").hasRole("ADMIN")

                        // Общие правила для DOCTOR и ADMIN
                        .requestMatchers("/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")

                        // Все остальные API запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                // --- НЕ используем сессии для API ---
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // --- Добавляем наш JWT фильтр ---
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}