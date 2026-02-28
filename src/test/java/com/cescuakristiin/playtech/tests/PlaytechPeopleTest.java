package com.cescuakristiin.playtech.tests;

import java.awt.Toolkit;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cescuakristiin.playtech.utils.DriverFactory;
import com.cescuakristiin.playtech.utils.TestOutput;

class PlaytechPeopleTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final TestOutput out = new TestOutput();

    @Test
    void assessmentTask_mainPlusTxtExport() throws Exception {

        driver = DriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        // Screen resolution (bonus requirement)
        java.awt.Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        out.section("Environment");
        out.line("Screen resolution: " + screen.width + "x" + screen.height);

        // 1) Open website
        out.section("1) Open website");
        driver.get("https://www.playtechpeople.com/");
        out.line("URL: " + driver.getCurrentUrl());
        out.line("Title: " + driver.getTitle());

        dismissCookieBannerIfPresent();

        // 2) Locations list
        out.section("2) Locations");
        List<String> locations = getLocationsListClean();
        out.line("Locations count: " + locations.size());
        locations.forEach(loc -> out.line("- " + loc));

        // 3) Casino description (Life at Playtech -> Who we are section -> Product Suite -> Casino)
        out.section("3) Casino product suite description");
        openLifeAtPlaytechPage();
        String casinoDescription = readCasinoDescription();
        out.line(casinoDescription);

        // 4) Estonia job links
        out.section("4) Estonia job links");
        openAllJobs();
        out.line("Tartu: " + findFirstJobLinkForCity("Tartu"));
        out.line("Tallinn: " + findFirstJobLinkForCity("Tallinn"));

        // Bonus: export results
        Path file = out.writeToFile("output/results.txt");
        out.section("Export");
        out.line("Results written to: " + file.toAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ---------------- Helper methods ----------------

    private void dismissCookieBannerIfPresent() {
        try {
            List<By> candidates = List.of(
                    By.cssSelector("#onetrust-accept-btn-handler"),
                    By.xpath("//button[contains(.,'Accept')]")
            );

            for (By by : candidates) {
                List<WebElement> elements = driver.findElements(by);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    elements.get(0).click();
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private List<String> getLocationsListClean() {
        // Stable source for the Locations list
        driver.get("https://www.playtechpeople.com/locations-countries/");

        List<WebElement> links = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("a[href*='/country/']")
        ));

        Set<String> hrefs = new LinkedHashSet<>();
        for (WebElement a : links) {
            String href = a.getAttribute("href");
            if (href != null && href.contains("/country/")) {
                hrefs.add(href);
            }
        }

        return hrefs.stream()
                .map(this::countryNameFromHref)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private String countryNameFromHref(String href) {
        int idx = href.indexOf("/country/");
        if (idx < 0) return null;

        String tail = href.substring(idx + "/country/".length());
        String slug = tail.split("/")[0];

        slug = slug.replace("-", " ").trim();
        return toTitleCase(slug);
    }

    private String toTitleCase(String text) {
        String[] parts = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isBlank()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (p.length() > 1) sb.append(p.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private void openLifeAtPlaytechPage() {
        driver.get("https://www.playtechpeople.com/life-at-playtech/");
        wait.until(ExpectedConditions.urlContains("life-at-playtech"));
    }

    private String readCasinoDescription() {
        // "Casino" is an h4 on this page (can vary), so match h2..h6
        By casinoHeadingBy = By.xpath("//*[self::h2 or self::h3 or self::h4 or self::h5 or self::h6][normalize-space()='Casino']");

        WebElement casinoHeading = wait.until(ExpectedConditions.presenceOfElementLocated(casinoHeadingBy));
        scrollIntoView(casinoHeading);

        // After scrolling, ensure it is visible (avoids "not visible yet" issues)
        casinoHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(casinoHeadingBy));

        // Description is the first paragraph after the heading on the page
        WebElement description = casinoHeading.findElement(By.xpath("following::*[self::p][1]"));
        scrollIntoView(description);

        return description.getText().trim();
    }

    private void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    private void openAllJobs() {
        driver.get("https://www.playtechpeople.com/jobs-our/");
        wait.until(ExpectedConditions.urlContains("jobs"));
    }

    private String findFirstJobLinkForCity(String city) {
        if (driver.getPageSource().contains("No Vacancies Found")) {
            return "No vacancies found";
        }

        try {
            WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[@href][ancestor-or-self::*[contains(.,'" + city + "')]]")
            ));
            return link.getAttribute("href");
        } catch (TimeoutException e) {
            return "No job link found for " + city;
        }
    }
}