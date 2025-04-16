package com.devicecontrol.service;

import com.codeborne.selenide.WebDriverProvider;
import com.devicecontrol.drivers.AndroidEmulatorDriver;
import com.devicecontrol.drivers.AndroidRealDriver;
import com.devicecontrol.drivers.IOSLocalDriver;
import com.devicecontrol.drivers.IOSSimulatorDriver;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.MutableCapabilities;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AppiumService {

    private final Map<String, AppiumDriver> drivers = new HashMap<>();

    public void startSession(String udid, String deviceType) throws Exception {
        // Завершаем существующую сессию, если она есть
        if (drivers.containsKey(udid)) {
            stopSession(udid);
        }
        System.out.println("Starting session for UDID: " + udid + ", Device Type: " + deviceType);
        try {
            WebDriverProvider driverProvider = getDriverProvider(deviceType);
            MutableCapabilities capabilities = new MutableCapabilities();
            AppiumDriver driver = (AppiumDriver) driverProvider.createDriver(capabilities);
            drivers.put(udid, driver);
            System.out.println("Session started successfully for UDID: " + udid);
            System.out.println("Session ID: " + driver.getSessionId());
            System.out.println("Capabilities: " + driver.getCapabilities().asMap());
        } catch (Exception e) {
            System.err.println("Failed to start session for UDID: " + udid + ". Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private WebDriverProvider getDriverProvider(String deviceType) {
        switch (deviceType) {
            case "ios-real":
                return new IOSLocalDriver();
            case "ios-simulator":
                return new IOSSimulatorDriver();
            case "android-emulator":
                return new AndroidEmulatorDriver();
            case "android-real":
                return new AndroidRealDriver();
            default:
                throw new IllegalArgumentException("Unknown device type: " + deviceType);
        }
    }

    public AppiumDriver getDriver(String udid) {
        AppiumDriver driver = drivers.get(udid);
        if (driver == null) {
            System.out.println("No driver found for UDID: " + udid);
            return null;
        }
        try {
            driver.getSessionId(); // Check if session is alive
            System.out.println("Driver found for UDID: " + udid + ", Session ID: " + driver.getSessionId());
            return driver;
        } catch (Exception e) {
            System.out.println("Session invalid for UDID: " + udid + ", removing driver");
            drivers.remove(udid);
            return null;
        }
    }

    public AppiumDriver getOrCreateDriver(String udid, String deviceType) throws Exception {
        AppiumDriver driver = getDriver(udid);
        if (driver == null) {
            System.out.println("No valid session for UDID: " + udid + ", attempting to create new session");
            startSession(udid, deviceType);
            driver = drivers.get(udid);
        }
        return driver;
    }

    public void stopSession(String udid) {
        AppiumDriver driver = drivers.get(udid);
        if (driver != null) {
            System.out.println("Stopping session for UDID: " + udid);
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error stopping session for UDID: " + udid + ": " + e.getMessage());
            }
            drivers.remove(udid);
            System.out.println("Session stopped for UDID: " + udid);
        }
    }

    public Map<String, Integer> getScreenSize(String udid) {
        AppiumDriver driver = getDriver(udid);
        if (driver != null) {
            try {
                Map<String, Integer> size = new HashMap<>();
                org.openqa.selenium.Dimension screenSize = driver.manage().window().getSize();
                size.put("width", screenSize.getWidth());
                size.put("height", screenSize.getHeight());
                System.out.println("Screen size for UDID " + udid + ": " + size);
                return size;
            } catch (Exception e) {
                System.err.println("Failed to get screen size for UDID: " + udid + ". Error: " + e.getMessage());
                return null;
            }
        }
        System.out.println("No driver found for UDID: " + udid);
        return null;
    }
}