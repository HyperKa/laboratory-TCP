package com.example.demo.service;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired @Lazy
    private ClientRepository clientRepository;

    @Autowired @Lazy
    private DoctorRepository doctorRepository;

    @Autowired @Lazy
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Проверка клиента
        Client client = clientRepository.findByLogin(username).orElse(null);
        if (client != null) {
            return new org.springframework.security.core.userdetails.User(
                    client.getLogin(),
                    client.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
            );
        }

        // Проверка доктора
        Doctor doctor = doctorRepository.findByLogin(username).orElse(null);
        if (doctor != null) {
            return new org.springframework.security.core.userdetails.User(
                    doctor.getLogin(),
                    doctor.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"))
            );
        }

        // Проверка админа
        Admin admin = adminRepository.findByLogin(username).orElse(null);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getLogin(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        throw new UsernameNotFoundException("User not found with login: " + username);
    }

    public void changePassword(String login, String role, String oldPassword, String newPassword) {
        switch (role.toUpperCase()) {
            case "ROLE_CLIENT" -> {
                Client client = clientRepository.findByLogin(login)
                        .orElseThrow(() -> new UsernameNotFoundException("Клиент не найден"));
                if (!passwordEncoder.matches(oldPassword, client.getPassword())) {
                    throw new IllegalArgumentException("Неверный старый пароль");
                }
                client.setPassword(passwordEncoder.encode(newPassword));
                clientRepository.save(client);
            }
            case "ROLE_DOCTOR" -> {
                Doctor doctor = doctorRepository.findByLogin(login)
                        .orElseThrow(() -> new UsernameNotFoundException("Доктор не найден"));
                if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
                    throw new IllegalArgumentException("Неверный старый пароль");
                }
                doctor.setPassword(passwordEncoder.encode(newPassword));
                doctorRepository.save(doctor);
            }
            case "ROLE_ADMIN" -> {
                Admin admin = adminRepository.findByLogin(login)
                        .orElseThrow(() -> new UsernameNotFoundException("Админ не найден"));
                if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
                    throw new IllegalArgumentException("Неверный старый пароль");
                }
                admin.setPassword(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
            }
            default -> throw new IllegalArgumentException("Неподдерживаемая роль: " + role);
        }
    }
}
