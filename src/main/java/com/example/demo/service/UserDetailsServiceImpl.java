package com.example.demo.service;

import com.example.demo.dto.ClientDTO;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.Role;
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
            System.out.println("Loaded user: " + username + ", Roles: " + client.getAuthorities());
            return new org.springframework.security.core.userdetails.User(
                    client.getLogin(),
                    client.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
            );
        }

        // Проверка доктора
        Doctor doctor = doctorRepository.findByLogin(username).orElse(null);
        if (doctor != null) {
            System.out.println("Loaded doctor: " + doctor.getLogin() + ", Roles: ROLE_DOCTOR");
            System.out.println("Found doctor: " + doctor.getLogin() + '\n' + doctor.getPassword());
            String rawPassword = "qwerty"; // Пароль, введенный пользователем
            String hashedPassword = doctor.getPassword(); // Хэшированный пароль из базы данных

            if (passwordEncoder.matches(rawPassword, hashedPassword)) {
                System.out.println("Пароль верный!");
            } else {
                System.out.println("Пароль неверный!");
            }
            return new org.springframework.security.core.userdetails.User(
                    doctor.getLogin(),
                    doctor.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"))
            );
        }

        // Проверка админа
        Admin admin = adminRepository.findByLogin(username).orElse(null);
        if (admin != null) {
            System.out.println("Loaded admin: " + admin.getLogin() + ", Roles: ROLE_ADMIN");
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




    // Регистрация клиента
    public void registerClient(ClientDTO clientDTO) {
        if (clientRepository.existsByLogin(clientDTO.getLogin())) {
            throw new RuntimeException("Login already exists");
        }

        Client client = new Client();
        client.setAge(clientDTO.getAge());
        client.setGender(clientDTO.getGender());
        client.setLastName(clientDTO.getLastName());
        client.setFirstName(clientDTO.getFirstName());
        client.setAddress(clientDTO.getAddress());
        client.setPassport(clientDTO.getPassport());
        client.setLogin(clientDTO.getLogin());
        client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        client.setRole(Role.CLIENT);

        clientRepository.save(client);
    }

    // Регистрация доктора
    public void registerDoctor(DoctorDTO doctorDTO, String adminUsername) {
        if (doctorRepository.existsByLogin(doctorDTO.getLogin())) {
            throw new RuntimeException("Login already exists");
        }

        if (!isAdmin(adminUsername)) {
            throw new RuntimeException("Only admins can register doctors");
        }
        Doctor doctor = new Doctor();
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        doctor.setExperience(doctorDTO.getExperience());
        doctor.setLogin(doctorDTO.getLogin());
        doctor.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        doctor.setRole(Role.DOCTOR);

        doctorRepository.save(doctor);
    }

    // Регистрация администратора
    public void registerAdmin(Admin admin) {
        if (adminRepository.existsByLogin(admin.getLogin())) {
            throw new RuntimeException("Login already exists");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
    }

    public boolean isAdmin(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
