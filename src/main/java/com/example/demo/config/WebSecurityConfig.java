// src/main/java/com/example/demo/config/WebSecurityConfig.java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(2) // Этот конфиг применяется вторым (для всего, что не /api/**)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF для простоты отключаем, но для продакшена лучше включить
                .authorizeHttpRequests(auth -> auth
                        // --- Правила для публичных ВЕБ-страниц ---
                        .requestMatchers("/", "/auth/login", "/auth/register/client").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // --- Правила авторизации для ВЕБ-страниц ---
                        .requestMatchers("/auth/register/doctor").hasRole("ADMIN")
                        .requestMatchers("/auth/register/admin").permitAll() // Временно для создания первого админа
                        .requestMatchers("/client/dashboard").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/web/**").hasAnyRole("CLIENT", "DOCTOR", "ADMIN") // Общее правило для всего раздела /web

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                // --- Механизм входа через форму ---
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("login") // <-- перегрузка имени для formLogin (по стандарту не login а username)
                        .defaultSuccessUrl("/client/dashboard", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // --- Механизм выхода ---
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )

                // --- Используем сессии ---
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }
}