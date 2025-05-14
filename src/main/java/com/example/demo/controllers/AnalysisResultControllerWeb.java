package com.example.demo.controllers;

import com.example.demo.dto.AnalysisResultRequest;
import com.example.demo.dto.AppointmentRecordDTO;
import com.example.demo.dto.DiseaseHistoryDTO;
import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.DiseaseHistory;
import com.example.demo.service.AnalysisResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.SchemaOutputResolver;
import java.util.List;

@Controller
@RequestMapping("/web/analysis-results")

public class AnalysisResultControllerWeb {

    @Autowired
    AnalysisResultService analysisResultService;

    @GetMapping
    public String showAllAnalysisResults(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("analysisResults", analysisResultService.getAllByClientUsername(username));
        return "analysis-results/list";
        //return "redirect:/client/dashboard";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("analysisResults", new AnalysisResultRequest());
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("Текущие роли: " + role);
        return "analysis-results/create";
    }

    @PostMapping
    public String createAnalysisResult(@ModelAttribute AnalysisResultRequest dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // при использовании Spring Security
        analysisResultService.createAnalysisResultAsDto(dto, username);
        System.out.println("Контроллер создания - analysisResults");
        // return "redirect:/web/appointments";    Да нахрен мне нужна эта таблица, уже есть ебейший лк
        return "redirect:/client/dashboard";
    }


    @GetMapping("/{id}")
    public String viewRecord(@PathVariable Long id, Model model) {
        AnalysisResultRequest dto = analysisResultService.getRecordByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Результаты анализа не найдены"));
        model.addAttribute("analysisResults", dto);
        return "analysis-results/view";
    }


    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<AnalysisResultRequest> getRecordById(@PathVariable Long id) {
        return analysisResultService.getRecordByIdAsDTO(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
    }



    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        AnalysisResultRequest dto = analysisResultService.getRecordByIdAsDTO(id)
                .orElseThrow(() -> new RuntimeException("Результаты анализа не найдены"));
        model.addAttribute("analysisResults", dto);
        return "analysis-results/edit";
    }

    @PostMapping("/{id}")
    //public String updateRecord(@PathVariable Long id, @ModelAttribute AnalysisResultRequest dto) {
    public String updateRecord(@PathVariable Long id, @ModelAttribute AnalysisResultRequest dto) {
        analysisResultService.updateAnalysisResult(id, dto);
        //return "redirect:/web/appointments";
        System.out.println("ID записи: " + id);
        System.out.println("DTO: " + dto.toString());

        if (dto.getResearchFile() == null || dto.getAnalysisDate() == null) {
            throw new IllegalArgumentException("Получены пустые данные");
        }
        return "redirect:/client/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteRecord(@PathVariable Long id) {
        analysisResultService.deleteAnalysisResult(id);
        //return "redirect:/web/appointments";
        return "redirect:/client/dashboard";
    }
}
