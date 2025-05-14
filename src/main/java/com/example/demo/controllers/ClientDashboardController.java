package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientDashboardController {

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

        ClientDTO clientDTO = clientService.findByLogin(username)
                .map(clientService::convertToDTO)
                .orElseThrow(() -> new RuntimeException("клиент не найден" + username));

        List<AppointmentRecordDTO> records = appointmentRecordService.getAppointmentsForClient(username);
        List<DiseaseHistoryDTO> diseaseHistories = diseaseHistoryService.getHistoryForClient(username);
        List<AnalysisResultRequest> analysisResults = analysisResultService.getResultsForClient(username);

        model.addAttribute("client", clientDTO);
        model.addAttribute("records", records);
        model.addAttribute("diseaseHistories", diseaseHistories);
        model.addAttribute("analysisResults", analysisResults);

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
        List<AppointmentRecordDTO> appointments = appointmentRecordService.getAppointmentsForClient(username);
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
        // Заполняем форму списком врачей и т.п.
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

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("client") ClientDTO clientDTO, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();

        clientService.updateClientFromDTO(username, clientDTO);

        redirectAttributes.addFlashAttribute("успешно", "Профиль обновлен");
        return "redirect:/client/dashboard";
    }

}

