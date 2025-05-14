package com.example.demo.controllers;


import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.service.AppointmentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/appointments")
public class AppointmentRecordControllerWeb {
    @Autowired
    private AppointmentRecordService appointmentRecordService;

    @GetMapping
    public String showAllAppointments(Model model) {
        model.addAttribute("records", appointmentRecordService.getAllRecordsAsDTO());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("appointmentRecord", new AppointmentRecordDTO());
        return "appointments/create";
    }

    @PostMapping
    public String createAppointment(@ModelAttribute AppointmentRecordDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // при использовании Spring Security
        appointmentRecordService.createRecordFromDTO(dto, username);
        // return "redirect:/web/appointments";    Да нахрен мне нужна эта таблица, уже есть ебейший лк
        return "redirect:/client/dashboard";
    }

    @GetMapping("/{id}")
    public String viewRecord(@PathVariable Integer id, Model model) {
        AppointmentRecordDTO dto = appointmentRecordService.getRecordByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
        model.addAttribute("record", dto);
        return "appointments/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        AppointmentRecordDTO dto = appointmentRecordService.getRecordByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
        model.addAttribute("appointmentRecord", dto);
        return "appointments/edit";
    }

    @PostMapping("/{id}")
    public String updateRecord(@PathVariable Integer id, @ModelAttribute AppointmentRecordDTO dto) {
        appointmentRecordService.updateRecordFromDTO(id, dto);
        //return "redirect:/web/appointments";
        return "redirect:/client/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteRecord(@PathVariable Integer id) {
        appointmentRecordService.deleteRecord(id);
        //return "redirect:/web/appointments";
        return "redirect:/client/dashboard";
    }
}
