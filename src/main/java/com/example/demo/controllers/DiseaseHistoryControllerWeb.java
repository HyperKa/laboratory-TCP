package com.example.demo.controllers;

import com.example.demo.dto.DiseaseHistoryDTO;
import com.example.demo.service.DiseaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/web/disease-history")
public class DiseaseHistoryControllerWeb {

    @Autowired
    private DiseaseHistoryService diseaseHistoryService;

    // Показать все истории болезни для клиента
    @GetMapping
    public String showAllHistories(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("diseaseHistories", diseaseHistoryService.getAllByClientUsername(username));
        return "disease-history/list"; // шаблон HTML, например templates/diseasehistory/list.html
    }

    // Показать форму создания
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("diseaseHistory", new DiseaseHistoryDTO());
        return "disease-history/create";
    }

    // Обработка формы создания
    @PostMapping
    public String createHistory(@ModelAttribute DiseaseHistoryDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        diseaseHistoryService.createFromWebDTO(dto, username);
        return "redirect:/client/dashboard"; // или "/web/disease-history"
    }

    // Показать конкретную историю болезни
    @GetMapping("/{id}")
    public String viewHistory(@PathVariable Long id, Model model) {
        DiseaseHistoryDTO dto = diseaseHistoryService.getRecordByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("История болезни не найдена"));
        model.addAttribute("diseaseHistory", dto);
        return "disease-history/view";
    }

    // Показать форму редактирования
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        DiseaseHistoryDTO dto = diseaseHistoryService.getRecordByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("История болезни не найдена"));
        model.addAttribute("diseaseHistory", dto);
        return "disease-history/edit";
    }

    // Обработка формы редактирования
    @PostMapping("/{id}")
    public String updateHistory(@PathVariable Long id, @ModelAttribute DiseaseHistoryDTO dto) {
        diseaseHistoryService.updateRecord(id, dto);
        return "redirect:/client/dashboard";
    }

    // Удаление
    @PostMapping("/{id}/delete")
    public String deleteHistory(@PathVariable Long id) {
        diseaseHistoryService.deleteRecord(id);
        return "redirect:/client/dashboard";
    }
}

