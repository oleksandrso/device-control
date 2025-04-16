package com.devicecontrol.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.net.URL;

public class IOSSimulatorDriver implements WebDriverProvider {

    public static final String APPIUM_SERVER_URL = "http://192.168.0.118:4723";
    public static final String DEVICE_NAME = "iPhone Xs";
    public static final String DEVICE_UDID = "A126EF10-7E87-4F00-9A39-F9600EA57FDA";


    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        MutableCapabilities desiredCapabilities = new MutableCapabilities();
        desiredCapabilities.merge(capabilities);

        desiredCapabilities.setCapability("platformName", "iOS");
        desiredCapabilities.setCapability("automationName", "XCUITest");
        desiredCapabilities.setCapability("platformVersion", "17.5");
        desiredCapabilities.setCapability("udid", DEVICE_UDID);
        desiredCapabilities.setCapability("deviceName", DEVICE_NAME);

        try {
            return new IOSDriver(new URL(APPIUM_SERVER_URL), desiredCapabilities);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать драйвер iOS симулятора: " + e.getMessage(), e);
        }
    }
}