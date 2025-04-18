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
                        // Public endpoints
                        .requestMatchers("/auth/login/**").permitAll()
                        .requestMatchers("/auth/register/client").permitAll()
                        .requestMatchers("/auth/register/admin").permitAll() // временно разрешаем для первого админа

                        // Доктора может регать только админ
                        .requestMatchers("/auth/register/doctor").hasRole("ADMIN")

                        // РОЛЬ КЛИЕНТА: доступ только к своим записям (GET)
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/analysis_results/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointment_records/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/disease_history/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/{id}").hasRole("CLIENT")

                        // РОЛЬ ДОКТОРА: доступ ко всем таблицам, но не может изменять свою таблицу
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа

                        // ОГРАНИЧЕНИЕ ДЛЯ ДОКТОРОВ: не могут создавать/редактировать/удалять записи в своей таблице
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").denyAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").denyAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").denyAll()

                        // РОЛЬ АДМИНА: полный доступ ко всем таблицам
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")

                    .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Без состояния

        return http.build();
    }

    /*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Используем BCrypt для хеширования паролей
    }

     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}