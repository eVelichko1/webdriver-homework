package ru.otus.homework.webdriver.driver;

/**
 * Режим запуска Chrome из задания.
 */
public enum ChromeLaunchMode {
    /** Обычный headless Chrome ({@code --headless=new}). */
    HEADLESS,
    /** Полноэкранный киоск без панелей браузера ({@code --kiosk}). */
    KIOSK,
    /** Окно Chrome на весь экран ({@code --start-fullscreen}). */
    FULL_SCREEN
}
