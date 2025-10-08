package org.example;

import ui.ConsoleInput;
import ui.ConsoleView;
import ui.LocalizationManager;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        LocalizationManager.loadBundle(new Locale("ru"));
        try (ConsoleInput input = new ConsoleInput()) {
            ConsoleView view = new ConsoleView();
            OchkoGame game = new OchkoGame(view, input);
            game.start();
        } catch (Exception e) {
            System.err.println("Произошла ошибка во время игры: " + e.getMessage());
        }
    }
}