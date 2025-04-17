package com.example.demo.config;


import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable()) // Отключаем CSRF для API


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login/**").permitAll()

                        .requestMatchers("/auth/register/client").permitAll()
                        .requestMatchers("/auth/register/admin").permitAll() // временно разрешаем для первого админа

                        // Доктора может регать только админ
                        .requestMatchers("/auth/register/doctor").hasRole("ADMIN")

                        // CLIENT
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/analysis_results/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointment_records/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/disease_history/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/{id}").hasRole("CLIENT")

                        // DOCTOR: доступ ко всем, кроме doctors (только GET)
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("DOCTOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").denyAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").denyAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").denyAll()

                        // ADMIN: полный доступ
                        .requestMatchers("/api/v1/**").hasRole("ADMIN")

                    .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Без состояния

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Используем BCrypt для хеширования паролей
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}