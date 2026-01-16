package github.ysbar.hangman.controller;

import github.ysbar.hangman.model.DataHolder;
import github.ysbar.hangman.service.HighscoreService;
import github.ysbar.hangman.service.SceneSwitcher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

/**
 * @AUTHOR: Yusuf
 * RESPONSIBILITY:
 * Displays the result of the round (Win/Loss), current session statistics,
 * and handles Highscore persistence.
 */

public class ScoreController {

    // --- FXML INJECTIONS ---
    @FXML private Label resultLabel;          // "YOU WON" or "GAME OVER"
    @FXML private Label currentScoreLabel;    // Total score of current session
    @FXML private Label highScoreLabel;       // Best score from file
    @FXML private Label wordsGuessedLabel;    // Words guessed this session
    @FXML private Label highScoreWordsLabel;  // Words guessed in highscore run
    @FXML private Label attemptsLabel;        // Lives left in the last round
    @FXML private Label timeLabel;            // Time taken in the last round

    // --- SERVICES ---
    private HighscoreService highscoreService = new HighscoreService();
    private SceneSwitcher sceneSwitcher = new SceneSwitcher();

    /**
     * Called when the ScoreScreen is loaded.
     * Populates all labels with data from DataHolder.
     */
    @FXML
    public void initialize() {
        // Retrieve global session state
        boolean win = DataHolder.isWin;
        int totalScore = DataHolder.totalScore;

        // Dynamic Text Styling based on result
        if (win) {
            resultLabel.setText("YOU WON!");
            resultLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: green;");
        } else {
            resultLabel.setText("GAME OVER");
            resultLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: red;");
        }

        // 1. Display Current Session Stats
        currentScoreLabel.setText("Score: " + totalScore);
        wordsGuessedLabel.setText("Words guessed: " + DataHolder.wordsGuessed);

        // Display stats from the specific round that just ended
        // (DataHolder stores these temporarily even if game is lost)
        attemptsLabel.setText("Attempts left: " + DataHolder.lastRemainingAttempts);
        timeLabel.setText("Time to finish: " + DataHolder.lastTime);

        // 2. Highscore Logic
        // Check if current session score beats the saved highscore
        highscoreService.checkAndSaveHighscore(totalScore, DataHolder.wordsGuessed);

        // Load the (potentially new) highscore from file and display it
        int[] stats = highscoreService.loadHighscore();
        highScoreLabel.setText("Score: " + stats[0]);
        highScoreWordsLabel.setText("Words guessed: " + stats[1]);
    }

    /**
     * Triggered by "Continue" button.
     * Starts a new round while keeping the current session score.
     */
    @FXML
    private void handlePlayAgain(ActionEvent event) {
        sceneSwitcher.switchTo("Gameplay", event);
    }

    /**
     * Triggered by "Quit" button.
     * Resets the entire session data (DataHolder.resetScore) and goes to Main Menu.
     */
    @FXML
    private void handleQuit(ActionEvent event) {
        DataHolder.resetScore();
        sceneSwitcher.switchTo("MainMenu", event);
    }
}