package com.devicecontrol.service;

import com.devicecontrol.drivers.IOSLocalDriver;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.MutableCapabilities;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AppiumService {

    private final Map<String, AppiumDriver> drivers = new HashMap<>();

    public void startSession(String udid) throws Exception {
        if (!drivers.containsKey(udid)) {
            System.out.println("Starting session for UDID: " + udid);
            try {
                // Используем IOSLocalDriver для создания драйвера
                IOSLocalDriver driverProvider = new IOSLocalDriver();
                // Передаем пустые capabilities, так как IOSLocalDriver сам их настраивает
                MutableCapabilities capabilities = new MutableCapabilities();
                AppiumDriver driver = (AppiumDriver) driverProvider.createDriver(capabilities);
                drivers.put(udid, driver);
                System.out.println("Session started successfully for UDID: " + udid);
                System.out.println("Session ID: " + driver.getSessionId());
                // Логируем capabilities для отладки
                System.out.println("Used Capabilities: " + driver.getCapabilities().asMap());
            } catch (Exception e) {
                System.err.println("Failed to start session for UDID: " + udid);
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } else {
            System.out.println("Session already exists for UDID: " + udid);
        }
    }

    public AppiumDriver getDriver(String udid) {
        AppiumDriver driver = drivers.get(udid);
        if (driver == null) {
            System.out.println("No driver found for UDID: " + udid);
        } else {
            System.out.println("Driver found for UDID: " + udid);
            System.out.println("Session ID: " + driver.getSessionId());
        }
        return driver;
    }

    public void stopSession(String udid) {
        AppiumDriver driver = drivers.get(udid);
        if (driver != null) {
            System.out.println("Stopping session for UDID: " + udid);
            driver.quit();
            drivers.remove(udid);
            System.out.println("Session stopped for UDID: " + udid);
        }
    }

    public Map<String, Integer> getScreenSize(String udid) {
        AppiumDriver driver = getDriver(udid);
        if (driver != null) {
            Map<String, Integer> size = new HashMap<>();
            org.openqa.selenium.Dimension screenSize = driver.manage().window().getSize();
            size.put("width", screenSize.getWidth());
            size.put("height", screenSize.getHeight());
            System.out.println("Screen size for UDID " + udid + ": " + size);
            return size;
        }
        System.out.println("No driver found for UDID: " + udid);
        return null;
    }
}