package com.devicecontrol.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.net.URL;

public class AndroidEmulatorDriver implements WebDriverProvider {

    public static final String EMULATOR_UDID = "EMULATOR_UDID"; // Замени на реальный UDID
    public static final String APPIUM_SERVER_URL = "http://192.168.0.118:4723";
    public static final String DEVICE_NAME = "Android Emulator";

    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        MutableCapabilities desiredCapabilities = new MutableCapabilities();
        desiredCapabilities.merge(capabilities);

        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("udid", EMULATOR_UDID);
        desiredCapabilities.setCapability("deviceName", DEVICE_NAME);
        desiredCapabilities.setCapability("noReset", true);

        try {
            return new AndroidDriver(new URL(APPIUM_SERVER_URL), desiredCapabilities);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать драйвер Android эмулятора: " + e.getMessage(), e);
        }
    }
}