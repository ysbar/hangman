package github.ysbar.hangman.controller;

import github.ysbar.hangman.model.DataHolder;
import github.ysbar.hangman.service.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.event.ActionEvent;

/**
 * @AUTHOR: Siqi
 * RESPONSIBILITY:
 * Handles the game setup phase.
 * It reads the user's selection from RadioButtons (Difficulty & Attempts),
 * saves these settings to the global DataHolder model, and starts the game.
 */

public class ConfigurationController {
    @FXML private RadioButton easyRadio, mediumRadio, hardRadio;
    @FXML private RadioButton wordEasy, wordMedium, wordHard;

    private SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    private void handleStartGame(ActionEvent event) {
        int attempts = 6;
        String diff = "Medium";

        // Determine Attempts based on UI selection
        if (easyRadio != null && easyRadio.isSelected()) attempts = 9;
        else if (hardRadio != null && hardRadio.isSelected()) attempts = 3;

        // Determine Difficulty based on UI selection
        if (wordEasy != null && wordEasy.isSelected()) diff = "Easy";
        else if (wordHard != null && wordHard.isSelected()) diff = "Hard";

        // Save settings to DataHolder (Model)
        DataHolder.targetAttempts = attempts;
        DataHolder.difficulty = diff;
        DataHolder.resetScore();

        // Switch to Game
        sceneSwitcher.switchTo("Gameplay", event);
    }
}