package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {
/*
    @RequestMapping("/login")
    public String loginForm(Model model) {

        return "login";  // отображаем страницу входа (login.html)

    }

 */
    @GetMapping("/auth/login")
    public String home() {
        return "login"; // Это имя шаблона без расширения (например, index.html)
    }
  /*  // Открытие страницы истории болезни по ID
    @GetMapping("/api/v1/disease-history/{recordId}")
    public String getDiseaseHistoryPage(@PathVariable("recordId") Long recordId) {
     return "disease-history";
    }

   */
    /*
    @GetMapping("/api/v1/clients/{id}")
    public String showClientDetails(@PathVariable Long id) {
        return "client-details"; // откроет client-details.html


    }


    @GetMapping("/api/v1/clients/me")
    public String showClientDetails() {
        return "client-details"; // client-details.html
    }
    */

    // Отображение HTML страницы профиля
    @GetMapping("/client-details")
    public ModelAndView showClientProfile() {
        return new ModelAndView("client-details");
    }
    @GetMapping("/disease-history/{recordId}")
    public String showRecordById(@PathVariable Long recordId) {
        return "disease-history"; // открывает, но только если через поисковую строку угадывать id доделать!!!!!!!!!!!

    }    @RequestMapping("/favourite_recipes")
    public String showFavRecipes() {
        return "favourite_recipes"; // thymeleaf найдет main.html
    }
    @RequestMapping("/profile")
    public String showMyProfile() {
        return "profile"; // thymeleaf найдет main.html
    }
    // Веб-метод для отображения страницы с рецептами категории
    @RequestMapping("/recipes/category/{categoryId}")
    public String getCategoryPage(@PathVariable("categoryId") Long categoryId) {

        // Возвращаем имя HTML-шаблона (например, category-recipes.html)
        return "recipes_category";
    }

    // Веб-метод для отображения страницы рецепта
    @RequestMapping("/getRecipe/{Id}")
    public String getRecipePage(@PathVariable("Id") Long id) {

        // Возвращаем имя HTML-шаблона (например, category-recipes.html)
        return "one_recipe";
    }
}