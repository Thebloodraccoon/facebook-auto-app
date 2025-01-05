package ua.thecoon.tech.task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.time.Duration;


public class FacebookAutomation implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(FacebookAutomation.class);

    @Value("${account.email}")
    private String email;

    @Value("${account.password}")
    private String password;

    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    @Value("${2captcha.apiKey}")
    private String captchaKey;

    private WebDriver driver;
    private CaptchaHandler captchaHandler;

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Starting Facebook automation...");

            WebDriverConfig config = new WebDriverConfig(chromeDriverPath);
            driver = config.setupDriver();

            captchaHandler = new CaptchaHandler(driver, captchaKey);

            loginToFacebook(email, password);

            if (captchaHandler.isCaptchaPresent()) {
                captchaHandler.handleCaptcha();
            }

            String photoUrl = getProfilePhotoUrl();

            PhotoDownloader.downloadPhoto(photoUrl);

            logger.info("Facebook automation completed successfully.");

        } catch (Exception e) {
            logger.error("An error occurred during automation: ", e);
        } finally {
            if (driver != null) {
                driver.quit();

                logger.info("Browser closed.");
            }
        }
    }


    private void loginToFacebook(String email, String password) {
        logger.info("Navigating to Facebook login page...");

        driver.get("https://www.facebook.com/login");

        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("pass"));
        WebElement loginButton = driver.findElement(By.name("login"));

        logger.info("Entering login credentials...");

        emailField.sendKeys(email);
        passwordField.sendKeys(password);

        logger.info("Clicking login button...");
        loginButton.click();
    }


    private String getProfilePhotoUrl() {
        logger.info("Navigating to profile page...");

        driver.get("https://www.facebook.com/me");

        logger.info("Locating profile photo...");

        WebElement svgElement = driver.findElement(By.cssSelector("svg[aria-label='Действия с фото профиля']"));
        svgElement.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement profilePhotoLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='photo/?fbid']")));

        profilePhotoLink.click();

        WebElement imageElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[data-visualcompletion='media-vc-image']")));

        String imageUrl = imageElement.getAttribute("src");

        logger.info("Profile photo URL: {}", imageUrl);
        return imageUrl;
    }

}
