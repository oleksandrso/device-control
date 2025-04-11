package com.devicecontrol.service;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppiumService implements WebDriverProvider {
    private final Map<String, AppiumDriver> drivers = new HashMap<>();

    public void startSession(String udid) throws Exception {
        if (!drivers.containsKey(udid)) {
            AppiumDriver driver = createDriver(createCapabilities(udid));
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

    @Override
    public AppiumDriver createDriver(Capabilities capabilities) {
        try {
            return new IOSDriver(new URL("http://192.168.0.118:4723"), capabilities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Appium driver", e);
        }
    }

    private Capabilities createCapabilities(String udid) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("UDID", udid);
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("usePrebuiltWDA", true);
        return capabilities;
    }
}