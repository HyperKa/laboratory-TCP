package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index() {
        return "main_page"; // Это имя шаблона Thymeleaf (без расширения .html)
    }
}