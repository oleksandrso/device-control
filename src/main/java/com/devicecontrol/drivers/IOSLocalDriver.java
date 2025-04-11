package com.devicecontrol.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.net.URL;


public class IOSLocalDriver implements WebDriverProvider {

    public static final String DEVICE_UDID = "86c1c3528327635aa5bcf664d5dd4b51cc7af0a3";
    public static final String APPIUM_SERVER_URL = "http://192.168.0.118:4723";
    public static final String DEVICE_NAME = "admin's iPhone";

    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        MutableCapabilities desiredCapabilities = new MutableCapabilities();
        desiredCapabilities.merge(capabilities);

        desiredCapabilities.setCapability("platformName", "iOS");
        desiredCapabilities.setCapability("automationName", "XCUITest");
        desiredCapabilities.setCapability("udid", DEVICE_UDID);
        desiredCapabilities.setCapability("deviceName", DEVICE_NAME);
        desiredCapabilities.setCapability("usePrebuiltWDA", true);
        desiredCapabilities.setCapability("noReset", true);

        try {
            return new IOSDriver(new URL(APPIUM_SERVER_URL), desiredCapabilities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create iOS driver: " + e.getMessage(), e);
        }
    }
}