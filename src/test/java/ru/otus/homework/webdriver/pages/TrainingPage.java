package ru.otus.homework.webdriver.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.otus.homework.webdriver.support.Colors;

/**
 * Страница тренажёра: https://otus.home.kartushin.su/training.html
 */
public class TrainingPage {

    public static final String URL = "https://otus.home.kartushin.su/training.html";

    private static final By TEXT_INPUT = By.id("textInput");
    private static final By OPEN_MODAL = By.id("openModalBtn");
    private static final By MODAL = By.id("myModal");
    private static final By MODAL_HEADING = By.cssSelector("#myModal h2");
    private static final By NAME = By.id("name");
    private static final By EMAIL = By.id("email");
    private static final By SUBMIT = By.xpath(
            "//form[@id='sampleForm']//button[normalize-space()='Отправить']"
    );
    private static final By MESSAGE = By.id("messageBox");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public TrainingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public TrainingPage open() {
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            driver.get(URL);
            String source = driver.getPageSource();
            if (source != null && source.contains("502 Bad Gateway")) {
                lastFailure = new IllegalStateException(
                        "Ресурс " + URL + " ответил 502 Bad Gateway (попытка " + attempt + ")"
                );
                continue;
            }
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(TEXT_INPUT));
                return this;
            } catch (RuntimeException error) {
                lastFailure = error;
            }
        }
        throw lastFailure == null
                ? new IllegalStateException("Не удалось открыть " + URL)
                : lastFailure;
    }

    public String enterText(String text) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(TEXT_INPUT));
        input.clear();
        input.sendKeys(text);
        return input.getDomProperty("value");
    }

    public String openModalButtonLabel() {
        return visibleText(wait.until(ExpectedConditions.visibilityOfElementLocated(OPEN_MODAL)));
    }

    public void openModal() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(OPEN_MODAL));
        scrollIntoView(button);
        button.click();
    }

    public boolean isModalShown() {
        List<WebElement> modals = driver.findElements(MODAL);
        return !modals.isEmpty() && modals.get(0).isDisplayed();
    }

    public boolean isModalDisplayed() {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL));
        return modal.isDisplayed();
    }

    public String modalHeading() {
        return visibleText(wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_HEADING)));
    }

    public String submitButtonLabel() {
        return visibleText(wait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT)));
    }

    public void submitForm(String name, String email) {
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NAME));
        nameInput.clear();
        nameInput.sendKeys(name);

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL));
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        scrollIntoView(submit);
        submit.click();
    }

    public String dynamicMessage() {
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(MESSAGE));
        wait.until(driver -> {
            String text = message.getText();
            return text != null && text.contains("Форма отправлена");
        });
        return message.getText().trim();
    }

    /**
     * Цвет фона сообщения. Если у самого блока фон прозрачный, берётся фон родителя.
     */
    public String messageBackgroundColor() {
        WebElement current = wait.until(ExpectedConditions.visibilityOfElementLocated(MESSAGE));
        for (int depth = 0; depth < 5; depth++) {
            String color = current.getCssValue("background-color");
            if (!Colors.isTransparent(color)) {
                return color;
            }
            if ("html".equalsIgnoreCase(current.getTagName())) {
                return color;
            }
            current = current.findElement(By.xpath(".."));
        }
        return current.getCssValue("background-color");
    }

    private void scrollIntoView(WebElement element) {
        if (driver instanceof JavascriptExecutor executor) {
            executor.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
        }
    }

    private static String visibleText(WebElement element) {
        return element.getText().replace('\u00a0', ' ').trim();
    }
}
