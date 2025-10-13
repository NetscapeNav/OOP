package ui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static ResourceBundle bundle;

    public static void loadBundle(Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String getString(String key) {
        return bundle.getString(key);
    }

    public static String getString(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }
}
