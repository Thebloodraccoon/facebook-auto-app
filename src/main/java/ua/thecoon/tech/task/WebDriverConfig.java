package ua.thecoon.tech.task;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class WebDriverConfig {
    private final String chromeDriverPath;

    private static final Logger logger = LoggerFactory.getLogger(WebDriverConfig.class);

    public WebDriver setupDriver() {
        logger.info("Setting up WebDriver...");

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logger.info("WebDriver setup completed.");
        return driver;
    }
}