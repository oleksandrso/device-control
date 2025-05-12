package com.devicecontrol.controller;

import com.devicecontrol.service.AppiumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceController {

    @Autowired
    private AppiumService appiumService;

    @GetMapping("/devices")
    public ResponseEntity<Map<String, Map<String, Map<String, String>>>> getDevices() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("static/devices.json").getInputStream();
            Map<String, Map<String, Map<String, String>>> devices = mapper.readValue(inputStream, Map.class);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            System.err.println("Failed to read devices.json: " + e.getMessage());
            return ResponseEntity.status(500).body(new HashMap<>());
        }
    }

    @PostMapping("/start_session")
    public ResponseEntity<Map<String, String>> startSession(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        String deviceType = request.get("deviceType");
        System.out.println("Starting session for UDID: " + udid + ", Device Type: " + deviceType);
        Map<String, String> response = new HashMap<>();
        try {
            appiumService.startSession(udid, deviceType);
            response.put("status", "success");
            response.put("message", "Session started for " + udid);
            System.out.println("Session started successfully for UDID: " + udid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Failed to start session for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/stop_session")
    public ResponseEntity<Map<String, String>> stopSession(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        System.out.println("Stopping session for UDID: " + udid);
        Map<String, String> response = new HashMap<>();
        try {
            appiumService.stopSession(udid);
            response.put("status", "success");
            response.put("message", "Session stopped for " + udid);
            System.out.println("Session stopped successfully for UDID: " + udid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Failed to stop session for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/tap")
    public ResponseEntity<Map<String, String>> tap(@RequestBody Map<String, Object> request) {
        String udid = (String) request.get("udid");
        Integer xObj = (Integer) request.get("x");
        Integer yObj = (Integer) request.get("y");
        if (xObj == null || yObj == null) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Missing x or y coordinates");
            System.out.println("Tap failed for UDID: " + udid + ". Missing coordinates.");
            return ResponseEntity.status(400).body(response);
        }
        int x = xObj;
        int y = yObj;
        String deviceType = (String) request.getOrDefault("deviceType", "ios-real");
        System.out.println("Tap request for UDID: " + udid + " at (" + x + ", " + y + ")");
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getOrCreateDriver(udid, deviceType);
            if (driver != null) {
                Map<String, Object> params = new HashMap<>();
                if (deviceType.startsWith("ios")) {
                    params.put("x", x);
                    params.put("y", y);
                    params.put("duration", 0);
                    driver.executeScript("mobile: tap", params);
                } else {
                    params.put("x", x);
                    params.put("y", y);
                    driver.executeScript("mobile: clickGesture", params);
                }
                response.put("status", "success");
                System.out.println("Tap successful for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "Session not found");
            System.out.println("Session not found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Tap failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/press_home")
    public ResponseEntity<Map<String, String>> pressHome(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        String deviceType = request.getOrDefault("deviceType", "ios-real");
        System.out.println("Home button press request for UDID: " + udid);
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getOrCreateDriver(udid, deviceType);
            if (driver != null) {
                if (deviceType.startsWith("ios")) {
                    driver.executeScript("mobile: pressButton", Map.of("name", "home"));
                } else {
                    driver.executeScript("mobile: pressKey", Map.of("keycode", 3));
                }
                response.put("status", "success");
                System.out.println("Home button pressed successfully for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "Session not found");
            System.out.println("Session not found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Home button press failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/press_back")
    public ResponseEntity<Map<String, String>> pressBack(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        String deviceType = request.getOrDefault("deviceType", "ios-real");
        System.out.println("Back button press request for UDID: " + udid);
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getOrCreateDriver(udid, deviceType);
            if (driver != null) {
                driver.executeScript("mobile: pressKey", Map.of("keycode", 4));
                response.put("status", "success");
                System.out.println("Back button pressed successfully for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "Session not found");
            System.out.println("Session not found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Back button press failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/press_menu")
    public ResponseEntity<Map<String, String>> pressMenu(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        String deviceType = request.getOrDefault("deviceType", "ios-real");
        System.out.println("Menu button press request for UDID: " + udid);
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getOrCreateDriver(udid, deviceType);
            if (driver != null) {
                driver.executeScript("mobile: pressKey", Map.of("keycode", 187));
                response.put("status", "success");
                System.out.println("Menu button pressed successfully for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "Session not found");
            System.out.println("Session not found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Menu button press failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/screenshot/{udid}")
    public ResponseEntity<byte[]> screenshot(@PathVariable String udid) {
        System.out.println("Screenshot request for UDID: " + udid);
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                File screenshot = driver.getScreenshotAs(OutputType.FILE);
                byte[] screenshotBytes = java.nio.file.Files.readAllBytes(screenshot.toPath());
                System.out.println("Screenshot captured successfully for UDID: " + udid);
                return ResponseEntity.ok()
                        .header("Content-Type", "image/png")
                        .body(screenshotBytes);
            }
            System.out.println("Session not found for UDID: " + udid);
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            System.err.println("Screenshot failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/screen_size/{udid}")
    public ResponseEntity<Map<String, Integer>> getScreenSize(@PathVariable String udid) {
        System.out.println("Screen size request for UDID: " + udid);
        try {
            Map<String, Integer> size = appiumService.getScreenSize(udid);
            if (size != null) {
                return ResponseEntity.ok(size);
            }
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            System.err.println("Failed to get screen size for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
