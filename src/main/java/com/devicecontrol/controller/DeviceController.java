package com.devicecontrol.controller;

import com.devicecontrol.service.AppiumService;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.OutputType;
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
        System.out.println("Received request to start session for UDID: " + udid);
        Map<String, String> response = new HashMap<>();
        try {
            appiumService.startSession(udid);
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
        System.out.println("Received request to stop session for UDID: " + udid);
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
        int x = (int) request.get("x");
        int y = (int) request.get("y");
        System.out.println("Received tap request for UDID: " + udid + " at coordinates (" + x + ", " + y + ")");
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                Map<String, Object> tapParams = new HashMap<>();
                tapParams.put("x", x);
                tapParams.put("y", y);
                ((IOSDriver) driver).executeScript("mobile: tap", tapParams);
                response.put("status", "success");
                System.out.println("Tap successful for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "No session found");
            System.out.println("No session found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Tap failed for UDID: " + udid + ". Error: " + e.getMessage());
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
        System.out.println("Received swipe request for UDID: " + udid + " from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")");
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                Map<String, Object> swipeParams = new HashMap<>();
                swipeParams.put("fromX", startX);
                swipeParams.put("fromY", startY);
                swipeParams.put("toX", endX);
                swipeParams.put("toY", endY);
                swipeParams.put("duration", 1.0);
                ((IOSDriver) driver).executeScript("mobile: dragFromToForDuration", swipeParams);
                response.put("status", "success");
                System.out.println("Swipe successful for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "No session found");
            System.out.println("No session found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Swipe failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/type")
    public ResponseEntity<Map<String, String>> typeText(@RequestBody Map<String, String> request) {
        String udid = request.get("udid");
        String text = request.get("text");
        System.out.println("Received type request for UDID: " + udid + " with text: " + text);
        Map<String, String> response = new HashMap<>();
        try {
            AppiumDriver driver = appiumService.getDriver(udid);
            if (driver != null) {
                Map<String, Object> typeParams = new HashMap<>();
                typeParams.put("value", text);
                ((IOSDriver) driver).executeScript("mobile: type", typeParams);
                response.put("status", "success");
                System.out.println("Type successful for UDID: " + udid);
                return ResponseEntity.ok(response);
            }
            response.put("status", "error");
            response.put("message", "No session found");
            System.out.println("No session found for UDID: " + udid);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("Type failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/screenshot/{udid}")
    public ResponseEntity<byte[]> screenshot(@PathVariable String udid) {
        System.out.println("Received screenshot request for UDID: " + udid);
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
            System.out.println("No session found for UDID: " + udid);
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            System.err.println("Screenshot failed for UDID: " + udid + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/screen_size/{udid}")
    public ResponseEntity<Map<String, Integer>> getScreenSize(@PathVariable String udid) {
        System.out.println("Received screen size request for UDID: " + udid);
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