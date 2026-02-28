package com.cescuakristiin.playtech.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        // akna suuruse fix 
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // me kasutame explicit wait'e

        return driver;
    }
}