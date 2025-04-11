import com.codeborne.selenide.Selenide;

import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;

public class MobileTestIOS extends MobileTestBaseIOS {


    @Test(description = "Test taking a screenshot from the iOS device")
    public void testTakeScreenshot() {
       Selenide.screenshot("screenshotEST");
    }

    @Test(description = "Test performing a tap on the iOS device")
    public void testPerformTap() {
        sleep(15000);
    }
}