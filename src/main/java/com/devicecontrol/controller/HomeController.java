package com.devicecontrol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/device-control")
    public String deviceControl() {
        return "device-control";
    }
}