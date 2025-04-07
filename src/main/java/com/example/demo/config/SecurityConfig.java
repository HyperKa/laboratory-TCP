package com.example.demo.config;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                /*
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/api/v1/doctors/**") // Отключаем CSRF для API
                        .ignoringRequestMatchers("/api/v1/clients/**")
                        .ignoringRequestMatchers("/api/v1/appointment_records/**")
                        .ignoringRequestMatchers("/api/v1/disease-history/**")
                        //analysis-results
                        .ignoringRequestMatchers("/api/v1/analysis-results/**")
                )
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/public/**").permitAll() // Разрешить доступ без аутентификации
                        .requestMatchers("/api/v1/doctors/**").permitAll() // Разрешить доступ к API врачей
                        .requestMatchers("/api/v1/clients/**").permitAll()
                        .requestMatchers("/api/v1/appointment_records/**").permitAll()
                        .requestMatchers("/api/v1/disease-history/**").permitAll()
                        .requestMatchers("/api/v1/analysis-results/**").permitAll()
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                )

                */
                /*
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    })
                )
                */
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF для API
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/v1/**"
//                            "/api/v1/doctors/**",
//                            "/api/v1/clients/**",
//                            "/api/v1/appointment-records/**",
//                            "/api/v1/disease-history/**",
//                            "/api/v1/analysis-results/**"
                            ).permitAll().anyRequest().authenticated() // Разрешить доступ к API врачей
                        //.requestMatchers("/api/v1/clients/**").permitAll()
                        //.requestMatchers("/api/v1/appointment-records/**").permitAll()
                        //.requestMatchers("/api/v1/disease-history/**").permitAll()
                        //.requestMatchers("/api/v1/analysis-results/**").permitAll()
                     // Все остальные запросы требуют аутентификации
                    )

                .formLogin(form -> form
                        .loginPage("/login") // Страница входа
                        .permitAll());
                //)
                //.logout(logout -> logout
                  //      .permitAll()
                //);
                /*
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll() // Разрешить доступ ко всем маршрутам
                );
                */

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Используем BCrypt для хеширования паролей
    }
}