# Facebook Automation with Spring Boot & Selenium

This project automates logging into Facebook and downloading the user's profile photo. The application is built with Spring Boot and Selenium WebDriver.

## CAPTCHA
First, need to check if a CAPTCHA exists on the page. This can be done by searching for CAPTCHA-related elements.
Next sending CAPTCHA to the service: send a CAPTCHA to the 2Captcha server via API, specifying the key and page URL.
Receiving the solution: after the service solves the CAPTCHA, receive the solution and enter it in the field on the page.

## Requirements
Before running the project, ensure you have the following:

- Java Development Kit (JDK) version 19.
- Apache Maven version 4.0.
- ChromeDriver (for Selenium WebDriver)
- Facebook account credentials

## Tools Used
- Spring Boot version 3.2.X
- Selenium WebDriver for automating browser actions.
- ChromeDriver for WebDriver configuration.

## Local Development

### Build

To build the project, execute the following command:

```bash
./mvnw --batch-mode clean package 
```

### Running

Environment variables

    DRIVER_PATH=<your-driver-path>
    EMAIL=<your-facebook-email>
    PASSWORD=<your-facebook-password>
  