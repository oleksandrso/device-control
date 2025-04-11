package com.devicecontrol.service;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

@Service
public class AppiumService {
    private final Map<String, AppiumDriver> drivers = new HashMap<>();

    public void startSession(String udid) throws Exception {
        if (!drivers.containsKey(udid)) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("UDID", udid);
            capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability("automationName", "XCUITest");
            capabilities.setCapability("usePrebuiltWDA", true);

            AppiumDriver driver = new IOSDriver(new URL("http://192.168.0.118:4723"), capabilities);
            drivers.put(udid, driver);
        }
    }

    public AppiumDriver getDriver(String udid) {
        return drivers.get(udid);
    }

    public void stopSession(String udid) {
        AppiumDriver driver = drivers.get(udid);
        if (driver != null) {
            driver.quit();
            drivers.remove(udid);
        }
    }
}