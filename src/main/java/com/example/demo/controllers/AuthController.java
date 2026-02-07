package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.Role;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.security.JwtTokenService;
import com.example.demo.service.BlacklistService;
import com.example.demo.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ChangePasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenService jwtTokenService;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @Autowired private ClientRepository clientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private BlacklistService blacklistService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtTokenService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }


    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody ClientDTO dto) {
        if (clientRepository.findByLogin(dto.getLogin()).isPresent()) {
            return ResponseEntity.badRequest().body("Client already exists");
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

        return ResponseEntity.ok(new JwtResponse(jwtTokenService.generateTokenFromLogin(client.getLogin(), "ROLE_CLIENT")));
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorDTO dto) {
        if (doctorRepository.findByLogin(dto.getLogin()).isPresent()) {
            return ResponseEntity.badRequest().body("Doctor already exists");
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

    @PostMapping("/register/admin")
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

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority()) // например: ROLE_CLIENT
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        try {
            userDetailsService.changePassword(username, role, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Пароль успешно изменен");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                LocalDateTime expiryDate = jwtTokenService.extractExpiration(token);
                blacklistService.addToBlacklist(token, expiryDate);
                return ResponseEntity.ok("Logged out successfully");
            } catch (ExpiredJwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token already expired");
            }
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }

}
