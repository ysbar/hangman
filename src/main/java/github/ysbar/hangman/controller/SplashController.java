package github.ysbar.hangman.controller;

import github.ysbar.hangman.service.SceneSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @AUTHOR: Yusuf
 * RESPONSIBILITY:
 * Handles the initial startup screen (Splash Screen).
 */

public class SplashController {
    @FXML private Label titleLabel;
    private SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void initialize() {
        // Create a new background Thread.
        new Thread(() -> {
            try {
                // Simulate a loading process or branding display for 3 seconds
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {

                try {
                    // 1. Get the current Stage from the label
                    Stage stage = (Stage) titleLabel.getScene().getWindow();

                    // 2. Switch to the Main Menu
                    sceneSwitcher.switchTo("MainMenu", stage);
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to switch from Splash to Menu.");
                    e.printStackTrace();
                }
            });
        }).start(); // Start the background thread
    }
}