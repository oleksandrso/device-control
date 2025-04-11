import com.devicecontrol.drivers.IOSLocalDriver;
import org.testng.annotations.BeforeSuite;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.open;


public class MobileTestBaseIOS {
    @BeforeSuite
    public void setup() {
        browser = IOSLocalDriver.class.getName();
        timeout = 18000;
        browserSize = null;
        open();
    }

}