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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ChangePasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/auth")
public class AuthControllerWeb {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenService jwtTokenService;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @Autowired private ClientRepository clientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private BlacklistService blacklistService;

    @PostMapping("/login/api")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtTokenService.generateToken(userDetails);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(login);
            String token = jwtTokenService.generateToken(userDetails);

            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(jwtCookie);

            return "redirect:/client/dashboard";
        } catch (Exception e) {
            return "redirect:/auth/login?error=invalid_credentials";  // сука и тут ошибку при использовании уже занятого логина нифига не понять
        }
    }


    @PostMapping("/register/client")
    public String registerClient(@ModelAttribute ClientDTO dto, HttpServletResponse response) {
        if (clientRepository.findByLogin(dto.getLogin()).isPresent()) {
            //return ResponseEntity.badRequest().body("Client already exists");
            return "redirect:/auth/register/client?error=Client+already+exists";
        }

        //System.out.println("Сохраняем клиента: " + dto.getLogin());

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

        System.out.println("Поля до save(): " + client);
        clientRepository.save(client);
        System.out.println("Клиент сохранён: " + client.getLogin());

        String token = jwtTokenService.generateTokenFromLogin(client.getLogin(), "ROLE_CLIENT");
        System.out.println("Сгенерирован токен: " + token);

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(jwtCookie);

        return "redirect:/client/dashboard";
        // return ResponseEntity.ok(new JwtResponse(jwtTokenService.generateTokenFromLogin(client.getLogin(), "ROLE_CLIENT")));
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@ModelAttribute DoctorDTO dto, HttpServletResponse response, Authentication authentication) {
        if (doctorRepository.findByLogin(dto.getLogin()).isPresent()) {
            return "redirect:/auth/register/doctor?error=Doctor+already+exists";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/client/dashboard?error=Only+admins+can+register+doctors";
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

        // Генерация JWT для доктора
        String token = jwtTokenService.generateTokenFromLogin(doctor.getLogin(), "ROLE_DOCTOR");

        // Установка куки
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(false); // чтобы можно было перенаправить
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(jwtCookie);

        return "redirect:/client/dashboard";
    }

    // 📝 Регистрация админа
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


    @GetMapping("/register/client")
    public String showClientRegistrationPage(Model model) {
        System.out.println("Rendering template: register_client");
        model.addAttribute("clientDTO", new ClientDTO());
        return "register_client"; // Имя Thymeleaf-шаблона для страницы регистрации клиента
    }

    @GetMapping("/register/doctor")
    public String showDoctorRegistrationPage(Model model) {
        model.addAttribute("doctorDTO", new DoctorDTO());
        return "register_doctor"; // шаблон register_doctor.html
    }

    // Админов добавлять только через API контроллер!!!
    /*
    @GetMapping("/register/admin")
    public String showAdminRegistrationPage() {
        return "register_admin"; // Имя Thymeleaf-шаблона для страницы регистрации админа
    }
    */

    @PostMapping("/change-password")
    public String changePassword(Model model, Authentication authentication) {
        System.out.println("Rendering template: register_client");
        model.addAttribute("clientDTO", new ClientDTO());
        return "change_password";
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        Cookie jwtCookie = new Cookie("jwt", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // переход на страницу login.html
    }
}
