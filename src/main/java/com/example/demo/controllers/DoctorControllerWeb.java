package com.example.demo.controllers;

import com.example.demo.dto.DoctorDTO;
import com.example.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import javax.xml.bind.SchemaOutputResolver;
import java.util.List;

@Controller
@RequestMapping("/web/doctors")

public class DoctorControllerWeb {

    @Autowired
    DoctorService doctorService;

    @GetMapping
    public String showAllAnalysisResults(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("doctors", doctorService.getAllDoctorsAsDTO());
        return "doctor/list";
        //return "redirect:/client/dashboard";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("doctors", new DoctorDTO());
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("Текущая роль: " + role);
        return "doctor/create";
    }

    @PostMapping
    public String createAnalysisResult(@ModelAttribute DoctorDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // при использовании Spring Security
        doctorService.createDoctorAsDTO(dto);
        System.out.println("Контроллер создания - doctors");
        // return "redirect:/web/appointments";    Да нахрен мне нужна эта таблица, уже есть ебейший лк
        return "redirect:/client/dashboard";
    }


    @GetMapping("/{id}")
    public String viewRecord(@PathVariable Long id, Model model) {
        DoctorDTO dto = doctorService.getDoctorByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Запись доктора не найдена"));
        model.addAttribute("doctors", dto);
        return "doctor/view";
    }


    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<DoctorDTO> getRecordById(@PathVariable Long id) {
        return doctorService.getDoctorByIdAsDTO(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Запись доктора в /api/{id} не найдена"));
    }



    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        DoctorDTO dto = doctorService.getDoctorByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Запись доктора в /{id}/edit не найдена"));
        model.addAttribute("doctors", dto);
        return "doctor/edit";
    }

    @PostMapping("/{id}")
    //public String updateRecord(@PathVariable Long id, @ModelAttribute AnalysisResultRequest dto) {
    public String updateRecord(@PathVariable Long id, @ModelAttribute DoctorDTO dto) {
        doctorService.updateDoctorAsDTO(id, dto);

        //return "redirect:/web/appointments";
        System.out.println("ID записи: " + id);
        System.out.println("DTO: " + dto.toString());

        if (dto.getLastName() == null || dto.getFirstName() == null  ||
                dto.getSpecialization() == null || dto.getExperience() == null ||
                dto.getLogin() == null || dto.getPassword() == null) {
            throw new IllegalArgumentException("Получены пустые данные");
        }
        return "redirect:/client/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteRecord(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        //return "redirect:/web/appointments";
        return "redirect:/client/dashboard";
    }
}
