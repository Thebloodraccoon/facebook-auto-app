package ua.thecoon.tech.task;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RequiredArgsConstructor
public class CaptchaHandler {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaHandler.class);

    private final WebDriver driver;
    private final String apiKey;

    private static final String API_URL = "http://2captcha.com/in.php";
    private static final String RESULT_URL = "http://2captcha.com/res.php";

    public boolean isCaptchaPresent() {
        try {
            logger.info("Checking for CAPTCHA...");

            driver.findElement(By.xpath("//div[contains(@class, 'captcha')]"));

            logger.warn("CAPTCHA detected!");
            return true;
        } catch (Exception e) {
            logger.info("No CAPTCHA detected.");
            return false;
        }
    }

    public String solveCaptcha() {
        try {
            logger.info("Sending CAPTCHA to 2Captcha for solving...");

            String captchaSolution = getCaptchaSolutionFrom2Captcha();

            logger.info("CAPTCHA solved successfully.");
            return captchaSolution;
        } catch (Exception e) {
            logger.error("Error solving CAPTCHA: ", e);
            return null;
        }
    }

    public void handleCaptcha() {
        logger.info("Handling CAPTCHA automatically...");

        while (isCaptchaPresent()) {
            String captchaSolution = solveCaptcha();
            if (captchaSolution != null) {
                driver.findElement(By.xpath("//input[@name='captcha_response']")).sendKeys(captchaSolution);
                driver.findElement(By.xpath("//button[@type='submit']")).click();
                break;
            }
        }
        logger.info("CAPTCHA resolved.");
    }

    private String getCaptchaSolutionFrom2Captcha() throws InterruptedException, IOException {
        String captchaId = sendCaptchaForSolving();
        if (captchaId != null) {
            return getCaptchaSolution(captchaId);
        }
        return null;
    }

    private String sendCaptchaForSolving() throws IOException {
        JSONObject json = new JSONObject();
        json.put("key", apiKey);
        json.put("method", "userrecaptcha");
        json.put("googlekey", "google_site_key");
        json.put("pageurl", "your_target_page_url");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(API_URL);
        post.setEntity(new StringEntity(json.toString()));
        post.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(post);
        String responseString = EntityUtils.toString(response.getEntity());

        JSONObject responseJson = new JSONObject(responseString);
        if (responseJson.getInt("status") == 1) {
            String captchaId = responseJson.getString("request");
            logger.info("Captcha sent for solving. Task ID: " + captchaId);
            return captchaId;
        } else {
            logger.error("Failed to send CAPTCHA for solving: " + responseJson.getString("request"));
            return null;
        }
    }

    private String getCaptchaSolution(String captchaId) throws InterruptedException, IOException {
        int attempts = 0;
        while (attempts < 10) {
            JSONObject json = new JSONObject();
            json.put("key", apiKey);
            json.put("action", "get");
            json.put("id", captchaId);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(RESULT_URL);
            post.setEntity(new StringEntity(json.toString()));
            post.setHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(post);
            String responseString = EntityUtils.toString(response.getEntity());

            JSONObject responseJson = new JSONObject(responseString);
            if (responseJson.getInt("status") == 1) {
                String captchaSolution = responseJson.getString("request");
                logger.info("CAPTCHA solved: " + captchaSolution);
                return captchaSolution;
            } else {
                Thread.sleep(5000);
                attempts++;
                logger.info("Waiting for CAPTCHA solution... Attempt " + attempts);
            }
        }

        logger.error("Failed to get CAPTCHA solution after 10 attempts.");
        return null;
    }
}
