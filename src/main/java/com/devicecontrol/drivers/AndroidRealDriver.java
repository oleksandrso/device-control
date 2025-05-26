package com.devicecontrol.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.net.URL;

public class AndroidRealDriver implements WebDriverProvider {

    public static final String DEVICE_UDID = "PJLZUSTGEQUSSSOR";
    public static final String APPIUM_SERVER_URL = "http://192.168.0.118:4723";
    public static final String DEVICE_NAME = "Android Device";

    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        MutableCapabilities desiredCapabilities = new MutableCapabilities();
        desiredCapabilities.merge(capabilities);

        desiredCapabilities.setCapability("platformName", "android");
        desiredCapabilities.setCapability("appium:automationName", "uiautomator2");
        desiredCapabilities.setCapability("appium:udid", DEVICE_UDID);
        desiredCapabilities.setCapability("appium:deviceName", DEVICE_NAME);
        desiredCapabilities.setCapability("appium:allowInsecure", "adb_shell");
        desiredCapabilities.setCapability("appium:newCommandTimeout", 600);
        desiredCapabilities.setCapability("appium:ignoreHiddenApiPolicyError", true);

        try {
            return new AndroidDriver(new URL(APPIUM_SERVER_URL), desiredCapabilities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Android real device driver: " + e.getMessage(), e);
        }
    }
}