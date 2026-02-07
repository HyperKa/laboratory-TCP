package com.example.demo.config;


import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Настройка доступа к URL
                .authorizeHttpRequests(auth -> auth
                        // 1. Разрешаем статику и главную
                        .requestMatchers("/css/**", "/js/**", "/favicon.ico", "/").permitAll()

                        // 2. Разрешаем вход и регистрацию клиента
                        .requestMatchers("/auth/login/**", "/auth/register/client", "/api/auth/login/**", "/api/auth/register/client").permitAll()

                        // 3. Просмотр (GET) разрешен всем ролям (Admin, Doctor, Client)
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").authenticated()

                        // 4. Управление врачами (Регистрация, удаление) - только АДМИН
                        .requestMatchers("/auth/register/doctor/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/doctors/**").hasRole("ADMIN")

                        // 5. Создание и редактирование записей (POST/PUT) - ВРАЧ и АДМИН
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAnyRole("DOCTOR", "ADMIN")

                        // 6. Удаление (DELETE) - только АДМИН (Врач не может удалять)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")

                        // 7. Все остальные запросы (Dashboard и прочее) должны быть просто авторизованы
                        .anyRequest().authenticated()
                )
                // Добавляем JWT фильтр
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Выключаем сессии
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Настройка выхода
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .deleteCookies("jwt")
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner dataLoader(
            com.example.demo.repository.AdminRepository adminRepo,
            com.example.demo.repository.DoctorRepository doctorRepo,
            com.example.demo.repository.ClientRepository clientRepo,
            com.example.demo.repository.AppointmentRecordRepository appointmentRepo,
            com.example.demo.repository.DiseaseHistoryRepository historyRepo,
            PasswordEncoder encoder) {
        return args -> {
            if (adminRepo.findByLogin("admin").isEmpty()) {
                com.example.demo.entity.Admin admin = new com.example.demo.entity.Admin();
                admin.setLogin("admin");
                admin.setPassword(encoder.encode("admin"));
                adminRepo.save(admin);
            }

            com.example.demo.entity.Doctor doctor = doctorRepo.findByLogin("doctor1")
                    .orElseGet(() -> {
                        com.example.demo.entity.Doctor d = new com.example.demo.entity.Doctor();
                        d.setLogin("doctor1");
                        d.setPassword(encoder.encode("1234"));
                        d.setFirstName("Иван");
                        d.setLastName("Иванов");
                        d.setSpecialization("Ветеринар-хирург");
                        d.setExperience("10 лет");
                        d.setRole(com.example.demo.entity.Role.DOCTOR);
                        return doctorRepo.save(d);
                    });

            com.example.demo.entity.Client client = clientRepo.findByLogin("client1")
                    .orElseGet(() -> {
                        com.example.demo.entity.Client c = new com.example.demo.entity.Client();
                        c.setLogin("client1");
                        c.setPassword(encoder.encode("1234"));
                        c.setFirstName("Лабрадор");
                        c.setLastName("Рекс");
                        c.setAge(5);
                        c.setGender("MALE");
                        c.setAddress("Владелец: Никита");
                        c.setPassport("CHIP-12345");
                        c.setRole(com.example.demo.entity.Role.CLIENT);
                        return clientRepo.save(c);
                    });

            if (appointmentRepo.findAll().isEmpty()) {
                String[] services = {"Осмотр", "Вакцинация", "Чистка ушей"};
                for (int i = 0; i < 3; i++) {
                    com.example.demo.entity.AppointmentRecord rec = new com.example.demo.entity.AppointmentRecord();
                    rec.setClient(client);
                    rec.setDoctor(doctor);
                    rec.setAppointmentDate(java.time.LocalDate.now().plusDays(i + 1));
                    rec.setAppointmentTime(java.time.LocalTime.of(10 + i, 0));
                    rec.setServiceName(services[i]);
                    appointmentRepo.save(rec);
                }
            }

            if (historyRepo.findAll().isEmpty()) {
                com.example.demo.entity.DiseaseHistory history = new com.example.demo.entity.DiseaseHistory();

                history.setDoctor(doctor);
                history.setClientId(client.getId());

                history.setFirstNameDoctor(doctor.getFirstName());
                history.setLastNameDoctor(doctor.getLastName());
                history.setProfession(doctor.getSpecialization());

                history.setDisease("Плановый осмотр и обработка");
                history.setStartDate(java.time.LocalDateTime.now().minusDays(2));
                history.setEndDate(java.time.LocalDateTime.now());

                historyRepo.save(history);
                System.out.println(">>> Тестовые данные (Ветклиника) успешно загружены!");
            }
        };
    }

}