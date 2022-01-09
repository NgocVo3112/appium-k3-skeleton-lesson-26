package driver;

import caps.MobileCapabilityTypeEx;
import flags.AndroidServerFlagEx;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    private AppiumDriver<MobileElement> appiumDriver;
    private static AppiumDriverLocalService appiumServer;

    public AppiumDriver<MobileElement> getAppiumDriver() {
        if (appiumDriver == null)
            appiumDriver = initAppiumDriver();
        return appiumDriver;
    }

    public AppiumDriver<MobileElement> getAppiumDriver(String udid, String port, String systemPort, String platformName, String platformVersion) {
        if (appiumDriver == null)
            appiumDriver = initAppiumDriver(udid, port, systemPort, platformName, platformVersion);
        return appiumDriver;
    }

    private AppiumDriver<MobileElement> initAppiumDriver() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        AppiumServiceBuilder appiumServerBuilder = new AppiumServiceBuilder();
        appiumServerBuilder.withArgument(AndroidServerFlagEx.ALLOW_INSECURE, "chromedriver_autodownload"); // Manage Chrome driver automatically
        appiumServerBuilder.withIPAddress("127.0.0.1").usingAnyFreePort();
        appiumServer = AppiumDriverLocalService.buildService(appiumServerBuilder);
        appiumServer.start();

        // Setup DesiredCapabilities
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.PLATFORM_NAME, "Android");
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.AUTOMATION_NAME, "uiautomator2");
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.UDID, "emulator-5554");
        // desiredCapabilities.setCapability("avd", "android_27"); // Automatically launch android virtual device - "android_27": avd name
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.APP_PACKAGE, "com.wdiodemoapp");
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.APP_ACTIVITY, "com.wdiodemoapp.MainActivity");
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.NEW_COMMAND_TIMEOUT, 120);
        appiumDriver = new AndroidDriver<>(appiumServer.getUrl(), desiredCapabilities);
        appiumDriver.manage().timeouts().implicitlyWait(3L, TimeUnit.SECONDS);
        return appiumDriver;
    }

    // Note: This one is infrastructure for parallel testing
    private AppiumDriver<MobileElement> initAppiumDriver(String udid, String port, String systemPort, String platformName, String platformVersion) {
        // Setup DesiredCapabilities
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.PLATFORM_NAME, platformName);
        desiredCapabilities.setCapability(
                MobileCapabilityTypeEx.AUTOMATION_NAME, platformName.equalsIgnoreCase("android") ? "uiautomator2" : "XCUITest");
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.SYSTEM_PORT, Integer.parseInt(systemPort));
        if (platformName.equalsIgnoreCase("android")) {
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.UDID, udid);
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.APP_PACKAGE, "com.wdiodemoapp");
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.APP_ACTIVITY, "com.wdiodemoapp.MainActivity");
        } else {
            desiredCapabilities.setCapability("wdaLocalPort", Integer.parseInt(systemPort));
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.PLATFORM_VERSION, platformVersion);
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.DEVICE_NAME, udid);
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.BUNDLE_ID, "org.wdioNativeDemoApp");
            desiredCapabilities.setCapability(MobileCapabilityTypeEx.NO_RESET, false);
            // desiredCapabilities.setCapability("app", "https://github.com/webdriverio/native-demo-app/releases/download/v0.4.0/iOS-Simulator-NativeDemoApp-0.4.0.app.zip");
        }
        desiredCapabilities.setCapability(MobileCapabilityTypeEx.NEW_COMMAND_TIMEOUT, 120);

        String localAppium = System.getenv("localAppium");
        String hub = System.getProperty("hub") == null ? System.getenv("hub") : System.getProperty("hub");

        String targetServer;
        if (localAppium != null) {
            targetServer = localAppium + "/wd/hub";
        } else if (hub != null) {
            targetServer = hub + ":4455/wd/hub";
        } else {
            throw new IllegalArgumentException("Please provide localAppium/hub");
        }

        try {
            URL appiumServerPath = new URL(targetServer);
            appiumDriver = new AndroidDriver<>(appiumServerPath, desiredCapabilities);
            appiumDriver.manage().timeouts().implicitlyWait(3L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appiumDriver;
    }

    public void quitAppiumSession() {
        if (appiumDriver != null) {
            appiumDriver.quit();
            appiumDriver = null;

            // stopAppiumServer(); // Note: When we have final infrastructure for parallel testing, this one is not necessary
        }
    }

    public static void stopAppiumServer() {
        String killNodeWindowsCmd = "taskkill /F /IM node.exe";
        String killNodeLinuxCmd = "killall node";

        String killNodeCmd = System.getProperty("os.name").toLowerCase().startsWith("windows")
                ? killNodeWindowsCmd : killNodeLinuxCmd;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(killNodeCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}