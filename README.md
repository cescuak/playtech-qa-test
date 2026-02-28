# Playtech QA Automation Assessment

## Overview

This project implements the 2026 DevQA Engineer assessment task using:

- Java 21
- Maven
- Selenium WebDriver
- JUnit 5

The test automates the required steps on https://www.playtechpeople.com and exports the results into a .txt file.

---

## Implemented Requirements

The automated test performs the following steps:

1. Opens https://www.playtechpeople.com  
2. Finds how many locations are listed under the "Locations" section and prints them  
3. Navigates to "Life at Playtech" and retrieves the Casino product suite description  
4. Opens "All Jobs" and prints a job link for Estonia (Tartu and Tallinn)  
5. Closes the browser  

The results are exported to:

output/results.txt

If no vacancies are available for Estonia at runtime, the test prints:

"No vacancies found"

Note: The job results depend on live website data and may change over time.

---

## Project Structure

playtech-qa-test
│
├── pom.xml
├── README.md
├── output/
│   └── results.txt
└── src
    └── test
        └── java
            └── com.cescuakristiin.playtech
                ├── tests
                │   └── PlaytechPeopleTest.java
                └── utils
                    ├── DriverFactory.java
                    └── TestOutput.java

---

## How to Run the Test

From the project root directory:

Run all tests:

mvn test

Or run only the assessment test:

mvn -Dtest=PlaytechPeopleTest test

After execution, the results file will be generated at:

output/results.txt

---

## Additional Notes

- The browser window is set to a fixed resolution for consistency.
- Screen resolution is logged at the beginning of the test execution.
- Explicit waits are used to improve test stability.
- The test reads live data from the website, so job availability may vary.