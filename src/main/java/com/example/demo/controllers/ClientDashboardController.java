package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entity.Client;
import com.example.demo.entity.Doctor;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientDashboardController {

    @Autowired private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DiseaseHistoryService diseaseHistoryService;

    @Autowired
    private AppointmentRecordService appointmentRecordService;

    @Autowired
    private AnalysisResultService analysisResultService;

    /*
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "client/dashboard"; // шаблон client_dashboard.html
    }
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {


        String username = principal.getName();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Роль не определена"));

        boolean isAdminOrDoctor = !"ROLE_CLIENT".equals(role);


        System.out.println("Current user: " + principal.getName());
        System.out.println("Roles: " + auth.getAuthorities());

        // список записей на приём в зависимости от роли логина клиента
        List<AppointmentRecordDTO> records;
        if ("ROLE_CLIENT".equals(role)) {
            Client client = clientService.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден: " + username));
            //records = appointmentRecordService.getAppointmentsForClient(client.getId());
            records = appointmentRecordService.getAppointmentsForUser(username, role);
        } else if ("ROLE_DOCTOR".equals(role)) {
            Doctor doctor = doctorService.findByLogin(username);
            //records = appointmentRecordService.getAppointmentsForDoctor(doctor.getId());
            records = appointmentRecordService.getAppointmentsForUser(username, role);
        } else if ("ROLE_ADMIN".equals(role)) {
            records = appointmentRecordService.getAllRecordsAsDTO(); // или getAllRecords()
        } else {
            throw new RuntimeException("Неизвестная роль: " + role);
        }

        // 2. История болезни
        List<DiseaseHistoryDTO> diseaseHistories;
        if ("ROLE_CLIENT".equals(role)) {
            diseaseHistories = diseaseHistoryService.getHistoryForClient(username);
        } else if ("ROLE_DOCTOR".equals(role)) {
            Doctor doctor = doctorService.findByLogin(username);
            diseaseHistories = diseaseHistoryService.getHistoryForDoctor(username);
        } else if ("ROLE_ADMIN".equals(role)) {
            diseaseHistories = diseaseHistoryService.getAllRecordsAsDTO();
        } else {
            throw new RuntimeException("Неизвестная роль");
        }

        // 3. Результаты анализов
        List<AnalysisResultRequest> analysisResults;
        if ("ROLE_CLIENT".equals(role)) {
            analysisResults = analysisResultService.getResultsForClient(username);
        } else if ("ROLE_DOCTOR".equals(role) || "ROLE_ADMIN".equals(role)) {
            analysisResults = analysisResultService.getAllRecordsAsDTO();
        } else {
            throw new RuntimeException("Неизвестная роль");
        }

        // 4. Список всех докторов
        List<DoctorDTO> doctors = doctorService.getAllDoctorsAsDTO();

        // 5. Профиль пользователя (только для CLIENT)
        ClientDTO clientDTO = null;
        if ("ROLE_CLIENT".equals(role)) {
            clientDTO = clientService.findByLogin(username)
                    .map(clientService::convertToDTO)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден: " + username));
        }

        // 6. Список всех клиентов (для DOCTOR и ADMIN)
        List<ClientDTO> allClients = List.of();
        if ("ROLE_ADMIN".equals(role)) {
            allClients = clientService.getAllClientsAsDTO();
        } else if ("ROLE_DOCTOR".equals(role)) {
            allClients = clientService.getClientsForDoctor(username);
            //allClients = clientService.getAllClientsAsDTO();
        }

        // 7. Передача данных в модель
        if (clientDTO != null) {
            model.addAttribute("client", clientDTO);
        }
        else {
            model.addAttribute("client", new ClientDTO());
        }

        model.addAttribute("role", role);  // для ролей на react
        model.addAttribute("records", records);
        model.addAttribute("diseaseHistories", diseaseHistories);
        model.addAttribute("analysisResults", analysisResults);
        model.addAttribute("doctors", doctors);
        model.addAttribute("allClients", allClients);
        model.addAttribute("isAdminOrDoctor", isAdminOrDoctor);


        return "client/dashboard";
    }

     @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        // По username получаем клиента
        String username = principal.getName();
        ClientDTO client = clientService.findByLogin(username)    // это необходимо для автозаполнения данных в форме
            .map(clientService::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Client not found with login: " + username));

        model.addAttribute("client", client);  // model - корзина данных, которые мы хотим передать на html, из нее берутся атрибуты для автозаполнения
        return "client/profile";
    }

    @GetMapping("/appointments")
    public String showAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Роль не определена"));
        List<AppointmentRecordDTO> appointments = appointmentRecordService.getAppointmentsForUser(username, role);
        model.addAttribute("appointments", appointments);
        return "client/appointments";
    }


    @GetMapping("/disease-history")
    public String showDiseaseHistory(Model model, Principal principal) {
        String username = principal.getName();
        List<DiseaseHistoryDTO> history = diseaseHistoryService.getHistoryForClient(username);
        model.addAttribute("history", history);
        return "client/disease-history";
    }


    /*
    @GetMapping("/disease-history")
    public String showDiseaseHistory(Model model, Principal principal) {
        String username = principal.getName();

        // Получаем текущую аутентификацию
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        List<DiseaseHistoryDTO> history;

        if ("ROLE_CLIENT".equals(role)) {
            // Клиент — только его история болезни
            history = diseaseHistoryService.getAllByClientUsername(username);

        } else if ("ROLE_DOCTOR".equals(role) || "ROLE_ADMIN".equals(role)) {
            // Доктор или админ — получаем по логину его ID и показываем все записи по нему
            Doctor doctor = doctorRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("Врач не найден: " + username));

            history = diseaseHistoryService.getAllByDoctorId(doctor.getId());

        } else {
            throw new RuntimeException("Неизвестная роль: " + role);
        }

        model.addAttribute("history", history);

        // Передача флага isAdmin/doctor для шаблона
        boolean isAdminOrDoctor = "ROLE_ADMIN".equals(role) || "ROLE_DOCTOR".equals(role);
        model.addAttribute("isAdminOrDoctor", isAdminOrDoctor);

        return "client/disease-history";
    }

     */

    @GetMapping("/analysis-results")
    public String showAnalysisResults(Model model, Principal principal) {
        String username = principal.getName();
        List<AnalysisResultRequest> results = analysisResultService.getResultsForClient(username);
        model.addAttribute("results", results);
        return "client/analysis-results";
    }

    @GetMapping("/doctors")
    public String showDoctors(Model model) {
        List<DoctorDTO> doctors = doctorService.getAllDoctorsAsDTO();
        model.addAttribute("doctors", doctors);
        return "client/doctors";
    }

    @GetMapping("/appointment/new")
    public String showAppointmentForm(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("appointment", new AppointmentRecordDTO());
        return "client/appointment_form";
    }

    @PostMapping("/appointment/new")
    public String createAppointment(
            @ModelAttribute("appointment") AppointmentRecordDTO appointmentDTO,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String username = principal.getName();
        appointmentRecordService.createRecordFromDTO(appointmentDTO, username);
        redirectAttributes.addFlashAttribute("success", "Запись успешно создана!");
        return "redirect:/client/appointments";
    }

    @PostMapping("/change-password")
    public String changePasswordWeb(@RequestParam String oldPassword,
                                    @RequestParam String newPassword,
                                    @RequestParam String confirmPassword,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {

        // --- 1. Проверка на совпадение нового пароля и подтверждения ---
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("ошибка", "Новый и старый пароли не совпадают!");
            return "redirect:/client/dashboard"; // Возвращаемся на дашборд с сообщением об ошибке
        }

        // --- 2. Получаем данные текущего пользователя ---
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        // --- 3. Вызываем сервис для смены пароля ---
        try {
            userDetailsService.changePassword(username, role, oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("успешно", "Пароль успешно изменен!");
        } catch (IllegalArgumentException e) {
            // Если сервис выбросил ошибку (например, "Неверный старый пароль")
            redirectAttributes.addFlashAttribute("ошибка", e.getMessage());
        }

        // --- 4. Возвращаемся на дашборд в любом случае ---
        return "redirect:/client/dashboard";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("client") ClientDTO clientDTO, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        /*
        Long clientId = clientDTO.getId();
        if (clientId == null) {
            throw new IllegalArgumentException("ID клиента не может быть null");
        }
        clientService.updateClientById(clientId, clientDTO);
        redirectAttributes.addFlashAttribute("успешно", "Профиль обновлен");
         */
        redirectAttributes.addFlashAttribute("успешно", "Профиль успешно обновлен");
        clientService.updateClientByLogin(username, clientDTO);
        return "redirect:/client/dashboard";
    }

}

