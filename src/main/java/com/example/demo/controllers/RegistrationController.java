package com.example.demo.controllers;

import com.example.demo.dto.ClientDTO;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.dto.JwtResponse;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.Role;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.security.JwtTokenService;
import com.example.demo.service.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/auth/register")
public class RegistrationController {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired private JwtTokenService jwtTokenService;
    @Autowired private ClientRepository clientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    private Class<Object> principal;

    // 📝 Регистрация клиента
    @PostMapping("/client")
    public String registerClient(@ModelAttribute ClientDTO dto, HttpServletResponse response) {
        if (clientRepository.findByLogin(dto.getLogin()).isPresent()) {
            //return ResponseEntity.badRequest().body("Client already exists");
            return "redirect:/auth/register/client?error=Client+already+exists";
        }

        Client client = new Client();
        client.setLogin(dto.getLogin());
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setAge(dto.getAge());
        client.setGender(dto.getGender());
        client.setLastName(dto.getLastName());
        client.setFirstName(dto.getFirstName());
        client.setAddress(dto.getAddress());
        client.setPassport(dto.getPassport());
        client.setRole(Role.CLIENT);

        clientRepository.save(client);

        String token = jwtTokenService.generateTokenFromLogin(client.getLogin(), "ROLE_CLIENT");

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(jwtCookie);

        return "redirect:/client/dashboard";
        // return ResponseEntity.ok(new JwtResponse(jwtTokenService.generateTokenFromLogin(client.getLogin(), "ROLE_CLIENT")));
    }

    // 📝 Регистрация доктора
    @PostMapping("/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorDTO dto) {
        if (doctorRepository.findByLogin(dto.getLogin()).isPresent()) {
            return ResponseEntity.badRequest().body("Doctor already exists");
        }

        if (!userService.isAdmin(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can register doctors");
        }

        Doctor doctor = new Doctor();
        doctor.setLogin(dto.getLogin());
        doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor.setLastName(dto.getLastName());
        doctor.setFirstName(dto.getFirstName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperience(dto.getExperience());
        doctor.setRole(Role.DOCTOR);

        doctorRepository.save(doctor);

        return ResponseEntity.ok(new JwtResponse(jwtTokenService.generateTokenFromLogin(doctor.getLogin(), "ROLE_DOCTOR")));
    }

    // 📝 Регистрация админа
    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin dto) {
        if (adminRepository.findByLogin(dto.getLogin()).isPresent()) {
            return ResponseEntity.badRequest().body("Admin already exists");
        }

        Admin admin = new Admin();
        admin.setLogin(dto.getLogin());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));

        adminRepository.save(admin);

        return ResponseEntity.ok(new JwtResponse(jwtTokenService.generateTokenFromLogin(admin.getLogin(), "ROLE_ADMIN")));
    }

    // 📝 Отображение формы регистрации клиента
    @GetMapping("/client")
    public String showClientRegistrationPage() {
        System.out.println("Rendering template: register_client");
        return "register_client"; // Имя Thymeleaf-шаблона для страницы регистрации клиента
    }

    // 📝 Отображение формы регистрации доктора
    @GetMapping("/doctor")
    public String showDoctorRegistrationPage() {
        return "register_doctor"; // Имя Thymeleaf-шаблона для страницы регистрации доктора
    }

    // 📝 Отображение формы регистрации админа
    @GetMapping("/admin")
    public String showAdminRegistrationPage() {
        return "register_admin"; // Имя Thymeleaf-шаблона для страницы регистрации админа
    }
}