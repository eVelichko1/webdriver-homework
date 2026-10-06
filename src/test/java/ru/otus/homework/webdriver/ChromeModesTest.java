package ru.otus.homework.webdriver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import ru.otus.homework.webdriver.driver.ChromeDriverFactory;
import ru.otus.homework.webdriver.driver.ChromeLaunchMode;
import ru.otus.homework.webdriver.pages.TrainingPage;
import ru.otus.homework.webdriver.support.Colors;

class ChromeModesTest {

    private static final String ENTERED_TEXT = "ОТУС";
    private static final String NAME = "фыв";
    private static final String EMAIL = "asdf@sdfg.rt";
    private static final String EXPECTED_MESSAGE =
            "Форма отправлена с именем: " + NAME + " и email: " + EMAIL;

    @Test
    @DisplayName("Headless: текст в поле ввода совпадает с «ОТУС»")
    void textInputMatchesEnteredValueInHeadlessChrome() {
        withChrome(ChromeLaunchMode.HEADLESS, driver -> {
            TrainingPage page = new TrainingPage(driver).open();

            String actual = page.enterText(ENTERED_TEXT);

            assertThat(actual).isEqualTo(ENTERED_TEXT);
        });
    }

    @Test
    @DisplayName("Киоск: по кнопке «Открыть модальное окно» открывается модальное окно")
    void modalOpensInKioskMode() {
        withChrome(ChromeLaunchMode.KIOSK, driver -> {
            TrainingPage page = new TrainingPage(driver).open();

            assertThat(page.openModalButtonLabel()).isEqualTo("Открыть модальное окно");
            assertThat(page.isModalShown()).isFalse();
            page.openModal();

            assertThat(page.isModalDisplayed()).isTrue();
            assertThat(page.modalHeading()).isEqualTo("Это модальное окно");
        });
    }

    @Test
    @DisplayName("Полный экран: после отправки формы появляется зелёное сообщение")
    void formShowsGreenMessageInFullscreen() {
        withChrome(ChromeLaunchMode.FULL_SCREEN, driver -> {
            TrainingPage page = new TrainingPage(driver).open();

            assertThat(page.submitButtonLabel()).isEqualTo("Отправить");
            page.submitForm(NAME, EMAIL);

            assertThat(page.dynamicMessage()).isEqualTo(EXPECTED_MESSAGE);

            String background = page.messageBackgroundColor();
            assertThat(Colors.isGreen(background))
                    .as("Динамическое сообщение должно быть на зелёном фоне, фактический фон: %s", background)
                    .isTrue();
        });
    }

    private static void withChrome(ChromeLaunchMode mode, Consumer<WebDriver> scenario) {
        WebDriver driver = ChromeDriverFactory.create(mode);
        try {
            scenario.accept(driver);
        } catch (RuntimeException | AssertionError error) {
            saveScreenshot(driver, mode);
            throw error;
        } finally {
            driver.quit();
        }
    }

    private static void saveScreenshot(WebDriver driver, ChromeLaunchMode mode) {
        if (!(driver instanceof TakesScreenshot screenshot)) {
            return;
        }
        try {
            Path directory = Path.of("target", "screenshots");
            Files.createDirectories(directory);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path file = directory.resolve(mode.name().toLowerCase() + "-" + stamp + ".png");
            Files.write(file, screenshot.getScreenshotAs(OutputType.BYTES));
        } catch (IOException | RuntimeException ignored) {
            // Снимок нужен только для разбора падения и не должен скрывать исходную ошибку.
        }
    }
}
