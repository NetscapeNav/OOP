package org.example;

import ui.ConsoleInput;
import ui.ConsoleView;

public class Main {
    public static void main(String[] args) {
        try (ConsoleInput input = new ConsoleInput()) {
            ConsoleView view = new ConsoleView();
            OchkoGame game = new OchkoGame(view, input);
            game.start();
        } catch (Exception e) {
            System.err.println("Произошла ошибка во время игры: " + e.getMessage());
        }
    }
}