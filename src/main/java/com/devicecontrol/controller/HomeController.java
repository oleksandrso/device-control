package com.devicecontrol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login.html"; // Перенаправляем на страницу логина
    }

    @GetMapping("/device-control")
    public String deviceControl() {
        return "index"; // Возвращаем index.html
    }
}