package com.example.demo.controllers;


import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.dto.ClientDTO;
import com.example.demo.entity.Client;
import com.example.demo.service.AppointmentRecordService;
import com.example.demo.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/clients")
public class ClientControllerWeb {
    @Autowired
    private ClientService clientService;

    @GetMapping
    public String showAllAppointments(Model model) {
        model.addAttribute("clients", clientService.getAllClients()
            .stream()
            .map(clientService::convertToDTO)
            .collect(Collectors.toList()));
        return "clients/list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("clients", new ClientDTO());
        return "clients/create";
    }

    @PostMapping
    public String createClient(@ModelAttribute ClientDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // при использовании Spring Security
        clientService.createClientFromDTO(dto);
        // return "redirect:/web/appointments";    Да нахрен мне нужна эта таблица, уже есть ебейший лк
        return "redirect:/client/dashboard";
    }

    @GetMapping("/{id}")
    public String viewRecord(@PathVariable Long id, Model model) {
        ClientDTO dto = clientService.getClientByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
        model.addAttribute("clients", dto);
        return "clients/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ClientDTO dto = clientService.getClientByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
        model.addAttribute("clients", dto);
        return "clients/edit";
    }

    @PostMapping("/{id}")
    public String updateRecord(@PathVariable Integer id, @ModelAttribute ClientDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("ID записи: " + id);
        clientService.updateClientFromDTO(username, dto);
        //return "redirect:/web/appointments";
        return "redirect:/client/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteRecord(@PathVariable Long id) {
        clientService.deleteClient(id);
        //return "redirect:/web/appointments";
        return "redirect:/client/dashboard";
    }
}
