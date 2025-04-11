package com.devicecontrol.service;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.devicecontrol.drivers.IOSLocalDriver;
import io.appium.java_client.AppiumDriver;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AppiumService {

    private final Map<String, AppiumDriver> drivers = new HashMap<>();

    public void startSession(String udid) throws Exception {
        if (!drivers.containsKey(udid)) {
            // Настраиваем Selenide для использования нашего драйвера
            Configuration.browser = IOSLocalDriver.class.getName();

            // Запускаем драйвер через Selenide
            AppiumDriver driver = (AppiumDriver) Selenide.webdriver().driver().getWebDriver();
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