package org.saucedemo.factories;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.saucedemo.factories.CapabilitiesFactory.getDesiredCapabilities;

@Slf4j
public class DriverFactory {
    public static AppiumDriver getDriver(String testClassName) {
        AppiumDriver driver;

        String PLATFORM_NAME = TestEnvironment.getConfig().getString("PLATFORM_NAME").toLowerCase();
        switch (PLATFORM_NAME) {
            case "android":
                driver = new AndroidDriver(getHostURL(), getDesiredCapabilities(testClassName));
                break;
            case "ios":
                driver = new IOSDriver(getHostURL(), getDesiredCapabilities(testClassName));
                break;
            default:
                throw new IllegalStateException(String.format("%s is not a valid platform choice. You can either choose 'android' or 'ios. " +
                        "Check the value of 'PLATFORM_NAME' property in application.conf; Or in CI, if running from continuous integration.", PLATFORM_NAME));
        }
        // Set implicit wait before returning the driver
        driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);

        log.info("SessionId: {}", driver.getSessionId());
        log.info("Driver capabilities: {}", driver.getCapabilities());
        return driver;
    }

    private static URL getHostURL() {
        try {
            return new URL(getHostUri());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("%s is Malformed host URL.", getHostUri()), e);
        }
    }

    private static String getHostUri() {
        String HOST_URI;
        String HOST = TestEnvironment.getConfig().getString("HOST").toLowerCase();
        switch (HOST) {
            case "saucelabs":
                String sauceUri = TestEnvironment.getConfig().getString("SAUCE_URI");
                HOST_URI = "https://" + System.getenv("SAUCE_USERNAME") + ":" + System.getenv("SAUCE_ACCESS_KEY") + sauceUri +"/wd/hub";
                log.info("HOST_URI: {}", HOST_URI);
                break;
            default:
                HOST_URI = TestEnvironment.getConfig().getString("HOST_URI");
        }
        return HOST_URI;
    }
}
