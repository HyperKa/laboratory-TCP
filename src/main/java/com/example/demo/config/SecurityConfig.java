package com.example.demo.config;

/*
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


                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")  // Вот тут тестить надо
                        //.requestMatchers(HttpMethod.GET, "/api/v1/disease-history/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        //.requestMatchers(HttpMethod.GET, "/api/v1/clients").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/analysis-results/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointment-records/{recordId}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/disease-history/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")



                        // ОГРАНИЧЕНИЕ ДЛЯ ДОКТОРОВ: не могут создавать/редактировать/удалять записи в своей таблице
                        //.requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").denyAll()
                        //.requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").denyAll()
                        //.requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").denyAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").hasRole("ADMIN")


                        // РОЛЬ ДОКТОРА: доступ ко всем таблицам, но не может изменять свою таблицу
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN") // Доступ к этим эндпоинтам и для доктора, и для админа


                        // РОЛЬ АДМИНА: полный доступ ко всем таблицам
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")
                        // тут было ограничение для доктора


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
/*
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

 */

import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ⬅️ вот так


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login/**").permitAll()
                        .requestMatchers("/auth/register/client").permitAll()
                        .requestMatchers("/auth/register/admin").permitAll()
                        .requestMatchers("/auth/register/doctor").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()
                        .requestMatchers("/api/v1/clients/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointment-records/my-records").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/analysis-results/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointment-records/{recordId}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/disease-history/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/v1/disease-history/client/**").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/v1/disease-history/doctor/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/{id}").hasAnyRole("CLIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

     */

    // 🔄 CORS конфигурация для поддержки запросов с credentials (cookies)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5500")); // или фронтовый адрес
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
