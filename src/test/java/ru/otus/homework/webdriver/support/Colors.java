package ru.otus.homework.webdriver.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Разбор CSS-цвета, который возвращает WebDriver ({@code rgb}/{@code rgba}).
 */
public final class Colors {

    private static final Pattern RGB = Pattern.compile(
            "rgba?\\(\\s*(\\d+)\\s*[, ]\\s*(\\d+)\\s*[, ]\\s*(\\d+)(?:\\s*[,/]\\s*([\\d.]+))?\\s*\\)"
    );

    private Colors() {
    }

    public static boolean isTransparent(String cssColor) {
        if (cssColor == null || cssColor.isBlank()) {
            return true;
        }
        String value = cssColor.trim().toLowerCase();
        if (value.equals("transparent")) {
            return true;
        }
        Matcher matcher = RGB.matcher(value);
        if (!matcher.find()) {
            return false;
        }
        double alpha = matcher.group(4) == null ? 1 : Double.parseDouble(matcher.group(4));
        return alpha == 0;
    }

    /**
     * Зелёный фон: непрозрачный цвет, в котором зелёный канал больше красного и синего.
     */
    public static boolean isGreen(String cssColor) {
        if (cssColor == null) {
            return false;
        }
        Matcher matcher = RGB.matcher(cssColor.trim().toLowerCase());
        if (!matcher.find()) {
            return false;
        }
        int red = Integer.parseInt(matcher.group(1));
        int green = Integer.parseInt(matcher.group(2));
        int blue = Integer.parseInt(matcher.group(3));
        double alpha = matcher.group(4) == null ? 1 : Double.parseDouble(matcher.group(4));
        return alpha > 0 && green > red && green > blue && green >= 80;
    }
}
