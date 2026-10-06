package ru.otus.homework.webdriver.driver;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Создаёт ChromeDriver с опцией, которую требует конкретный сценарий.
 * Общие флаги нужны, чтобы Chrome стабильно стартовал в Linux-контейнере
 * и не меняют запрошенный режим.
 */
public final class ChromeDriverFactory {

    private ChromeDriverFactory() {
    }

    public static WebDriver create(ChromeLaunchMode mode) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--no-first-run",
                "--no-default-browser-check",
                "--disable-search-engine-choice-screen",
                "--lang=ru"
        );

        switch (mode) {
            case HEADLESS -> {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
            }
            case KIOSK -> options.addArguments("--kiosk");
            case FULL_SCREEN -> options.addArguments("--start-fullscreen");
            default -> throw new IllegalArgumentException("Неизвестный режим Chrome: " + mode);
        }

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
        return driver;
    }
}
