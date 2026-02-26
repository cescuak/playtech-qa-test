package com.cescuakristiin.playtech;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

class SmokeTest {

    private WebDriver driver;

    @Test
    void openHomePage() {
        driver = new ChromeDriver();
        driver.get("https://www.playtechpeople.com/");
        System.out.println("Title: " + driver.getTitle());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}