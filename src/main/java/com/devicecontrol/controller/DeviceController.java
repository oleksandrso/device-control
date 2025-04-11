package com.devicecontrol.controller;

import com.codeborne.selenide.Selenide;
import com.devicecontrol.service.AppiumService;
import io.appium.java_client.AppiumDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceController {

    @Autowired
    private AppiumService appiumService;

    @PostMapping("/start_session")
    public ResponseEntity<Map<String, String>> startSession(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        Map<String, String> response = new HashMap<>();
        try {
            appiumService.startSession(udid);
            response.put("status", "success");
            response.put("message", "Session started for " + udid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/tap")
    public ResponseEntity<Map<String, String>> tap(@RequestBody Map<String, Object> request) {
        String udid = (String) request.get("udid");
        int x = (int) request.get("x");
        int y = (int) request.get("y");
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                Selenide.executeJavaScript("mobile: tap", Map.of("x", x, "y", y));
                response.put("status", "success");
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "No session found");
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/swipe")
    public ResponseEntity<Map<String, String>> swipe(@RequestBody Map<String, Object> request) {
        String udid = (String) request.get("udid");
        int startX = (int) request.get("start_x");
        int startY = (int) request.get("start_y");
        int endX = (int) request.get("end_x");
        int endY = (int) request.get("end_y");
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                Selenide.executeJavaScript("mobile: dragFromToForDuration", Map.of(
                        "fromX", startX,
                        "fromY", startY,
                        "toX", endX,
                        "toY", endY,
                        "duration", 1.0
                ));
                response.put("status", "success");
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "No session found");
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/type")
    public ResponseEntity<Map<String, String>> typeText(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        String text = request.get("text");
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                Selenide.executeJavaScript("mobile: type", Map.of("value", text));
                response.put("status", "success");
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "No session found");
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/screenshot/{udid}")
    public ResponseEntity<byte[]> screenshot(@PathVariable String udid) {
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                File screenshot = driver.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                byte[] screenshotBytes = java.nio.file.Files.readAllBytes(screenshot.toPath());
                return ResponseEntity.ok()
                        .header("Content-Type", "image/png")
                        .body(screenshotBytes);
            }
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}