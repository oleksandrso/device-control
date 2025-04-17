package com.devicecontrol.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, String> response = new HashMap<>();

        // Простая проверка учетных данных (замените на реальную логику с базой данных)
        if ("admin".equals(username) && "password".equals(password)) {
            // Здесь можно сгенерировать токен (например, JWT) в реальном приложении
            response.put("status", "success");
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Boolean>> checkAuth() {
        // Здесь должна быть проверка токена или сессии
        // Для демонстрации всегда возвращаем true, если пользователь "авторизован"
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthenticated", true); // Замените на реальную проверку
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Здесь должна быть логика завершения сессии или аннулирования токена
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
}