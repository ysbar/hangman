package github.ysbar.hangman.controller;

import github.ysbar.hangman.service.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.application.Platform;

/**
 * @AUTHOR: Julian
 * RESPONSIBILITY:
 * Handles interactions on the Main Menu screen.
 */

public class MenuController {
    private SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    private void handleNewGame(ActionEvent event) {
        // Navigate to Configuration Screen
        sceneSwitcher.switchTo("Configuration", event);
    }

    @FXML
    private void handleQuit() {
        // Clean application exit
        Platform.exit();
    }
}