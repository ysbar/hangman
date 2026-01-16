package github.ysbar.hangman.controller;

import github.ysbar.hangman.model.DataHolder;
import github.ysbar.hangman.service.SceneSwitcher;
import github.ysbar.hangman.service.WordService;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @AUTHOR: Julian
 * RESPONSIBILITY:
 * This class manages the active gameplay session. It handles user input
 * (keyboard clicks), updates the game state (score, lives), draws the
 * hangman figure incrementally, and manages the game timer.
 */

public class GameController {

    // --- FXML INJECTIONS (View Elements) ---
    @FXML private Label timerLabel;    // Displays elapsed time
    @FXML private Label guessesLabel;  // Displays remaining lives
    @FXML private Text wordDisplay;    // Displays the masked word (e.g. "_ A V _")
    @FXML private VBox keyboardBox;    // Container for dynamic keyboard buttons
    @FXML private Pane drawingPane;    // Area where the hangman is drawn
    @FXML private Button hintButton;   // Button to trigger a hint

    // --- SERVICES (Dependency Injection / Separation of Concerns) ---
    private WordService wordService = new WordService();     // Handles file I/O to get words
    private SceneSwitcher sceneSwitcher = new SceneSwitcher(); // Handles scene transitions

    // --- GAME STATE VARIABLES ---
    private String secretWord;          // The actual word to guess (e.g., "JAVA")
    private StringBuilder currentGuess; // The current state of the guess (e.g., "_ A _ A")
    private int maxAttempts;            // Total allowed lives
    private int remainingAttempts;      // Current lives left
    private long startTime;             // Timestamp when the round started
    private AnimationTimer timer;       // JavaFX timer for real-time updates

    /**
     * JavaFX Lifecycle Method: initialize()
     * Called automatically after the FXML file is loaded.
     * Starts the game setup immediately.
     */
    public void initialize() {
        // Fetch settings from the global DataHolder model
        setupGame(DataHolder.difficulty, DataHolder.targetAttempts);
    }

    /**
     * Resets the game state for a new round.
     * @param difficulty The selected difficulty level (affects word choice).
     * @param attempts The number of lives allowed.
     */
    public void setupGame(String difficulty, int attempts) {
        this.maxAttempts = attempts;
        this.remainingAttempts = attempts;

        // Step 1: Get a random word using the WordService
        this.secretWord = wordService.getRandomWord(difficulty);

        // Step 2: Create the masked string (underscores)
        currentGuess = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            currentGuess.append("_");
        }

        // Step 3: Update UI elements
        wordDisplay.setText(formatCurrentGuess());
        // Inline styling for the specific font and size requested
        wordDisplay.setStyle("-fx-fill: black; -fx-font-size: 48px; -fx-font-family: 'Verdana';");

        // Step 4: Initialize game components
        createQwertyKeyboard();     // Generate buttons
        startTimer();               // Start the clock
        updateStats();              // Show initial lives
        drawingPane.getChildren().clear(); // Clear previous drawings
    }

    /**
     * HINT SYSTEM
     * Logic: Reveals one random missing letter.
     * Penalty: Costs 2 lives (Attempts).
     * Usage: Can only be used if player has > 2 lives.
     */
    @FXML
    private void handleHint() {
        // Guard Clause: Prevent usage if not enough lives
        if (remainingAttempts <= 2) return;

        // 1. Identify all indices that are still hidden ('_')
        List<Integer> missingIndices = new ArrayList<>();
        for (int i = 0; i < secretWord.length(); i++) {
            if (currentGuess.charAt(i) == '_') {
                missingIndices.add(i);
            }
        }

        if (!missingIndices.isEmpty()) {
            // 2. Pick a random index from the missing ones
            int randomIndex = missingIndices.get(new Random().nextInt(missingIndices.size()));
            char letterToReveal = secretWord.charAt(randomIndex);

            // 3. Process this letter as a correct guess
            handleGuessLogic(letterToReveal);

            // 4. Apply the penalty manually
            // (handleGuessLogic doesn't deduct lives for correct guesses, so we do it here)
            remainingAttempts -= 2;

            // 5. Update UI and disable the key on the virtual keyboard
            updateStats();
            drawHangman();
            disableKey(letterToReveal);
        }
    }

    /**
     * Dynamically creates the on-screen QWERTY keyboard.
     * This avoids hardcoding 26 buttons in FXML.
     */
    private void createQwertyKeyboard() {
        keyboardBox.getChildren().clear();
        String[] rows = { "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM" };

        for (String rowChars : rows) {
            // Create a horizontal row
            HBox rowBox = new HBox(5);
            rowBox.setAlignment(javafx.geometry.Pos.CENTER);

            // Create a button for each char in the row
            for (char c : rowChars.toCharArray()) {
                Button btn = new Button(String.valueOf(c));
                btn.getStyleClass().add("key-button"); // Apply CSS class
                btn.setMinWidth(40);
                btn.setMinHeight(40);

                // Attach event listener: When clicked, handle the guess
                btn.setOnAction(e -> handleGuess(c, btn));

                rowBox.getChildren().add(btn);
            }
            // Add row to the main VBox
            keyboardBox.getChildren().add(rowBox);
        }
    }

    /**
     * Helper to visually disable a specific key on the virtual keyboard.
     */
    private void disableKey(char c) {
        // Iterate through rows (HBoxes)
        for (javafx.scene.Node row : keyboardBox.getChildren()) {
            HBox hBox = (HBox) row;
            // Iterate through buttons in row
            for (javafx.scene.Node node : hBox.getChildren()) {
                Button btn = (Button) node;
                // If text matches, disable it
                if (btn.getText().equals(String.valueOf(c))) {
                    btn.setDisable(true);
                }
            }
        }
    }

    /**
     * Wrapper for guess logic triggered by Button click.
     */
    private void handleGuess(char letter, Button btn) {
        btn.setDisable(true); // Disable button immediately to prevent double clicks
        handleGuessLogic(letter);
    }

    /**
     * Core Game Logic: Checks if the guessed letter is in the secret word.
     */
    private void handleGuessLogic(char letter) {
        boolean correct = false;

        // Loop through secret word to find matches
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                currentGuess.setCharAt(i, letter); // Reveal character
                correct = true;
            }
        }

        if (correct) {
            // Update display
            wordDisplay.setText(formatCurrentGuess());
            // Check Win Condition
            if (currentGuess.toString().equals(secretWord)) {
                gameOver(true);
            }
        } else {
            // Incorrect guess
            remainingAttempts--;
            drawHangman(); // Draw next part of the body
            // Check Lose Condition
            if (remainingAttempts <= 0) {
                gameOver(false);
            }
        }
        // Update stats labels (guesses left)
        updateStats();
    }

    /**
     * Draws the Hangman incrementally based on the percentage of lives lost.
     * This logic works for ANY number of max attempts (3, 6, or 9).
     */
    private void drawHangman() {
        int mistakes = maxAttempts - remainingAttempts;
        // Calculate percentage of game lost (0.0 to 1.0)
        double percentageLost = (double) mistakes / maxAttempts;
        // Map percentage to 10 drawing stages
        int stagesToShow = (int) Math.ceil(percentageLost * 10);

        // Center coordinates for drawing
        double w = drawingPane.getWidth();
        double h = drawingPane.getHeight();
        double cx = w / 2;
        double strokeWidth = 4.0;

        // Progressive Drawing: Add lines based on current stage
        if (stagesToShow >= 1) addLine(cx-60, h-20, cx+60, h-20, strokeWidth); // Base
        if (stagesToShow >= 2) addLine(cx-40, h-20, cx-40, 20, strokeWidth);   // Pole
        if (stagesToShow >= 3) addLine(cx-40, 20, cx, 20, strokeWidth);        // Top bar
        if (stagesToShow >= 4) addLine(cx, 20, cx, 50, strokeWidth);           // Rope

        if (stagesToShow >= 5) { // Head (Circle)
            Circle head = new Circle(cx, 75, 25);
            head.setFill(null);
            head.setStroke(javafx.scene.paint.Color.BLACK);
            head.setStrokeWidth(strokeWidth);
            drawingPane.getChildren().add(head);
        }

        if (stagesToShow >= 6) addLine(cx, 100, cx, 170, strokeWidth);         // Body
        if (stagesToShow >= 7) addLine(cx, 120, cx-35, 150, strokeWidth);      // Left Arm
        if (stagesToShow >= 8) addLine(cx, 120, cx+35, 150, strokeWidth);      // Right Arm
        if (stagesToShow >= 9) addLine(cx, 170, cx-35, 220, strokeWidth);      // Left Leg
        if (stagesToShow >= 10) addLine(cx, 170, cx+35, 220, strokeWidth);     // Right Leg
    }

    // Helper to add a JavaFX Line to the pane
    private void addLine(double sx, double sy, double ex, double ey, double width) {
        Line line = new Line(sx, sy, ex, ey);
        line.setStrokeWidth(width);
        line.setStrokeLineCap(StrokeLineCap.ROUND); // Rounds the line ends for smoother look
        drawingPane.getChildren().add(line);
    }

    // Helper to add spaces between letters for better readability (e.g., "J A V A")
    private String formatCurrentGuess() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentGuess.length(); i++) {
            sb.append(currentGuess.charAt(i)).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Starts the game timer using AnimationTimer.
     * AnimationTimer runs once per frame (approx. 60fps), allowing precise time tracking.
     */
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = (System.currentTimeMillis() - startTime) / 1000; // convert to seconds
                // Format as MM:SS
                timerLabel.setText(String.format("Time passed: %02d:%02d", elapsed / 60, elapsed % 60));
            }
        };
        timer.start();
    }

    // Updates the "Guesses left" label and disables hint button if lives are low
    private void updateStats() {
        guessesLabel.setText("Guesses left: " + remainingAttempts);
        if (hintButton != null) {
            // Disable hint if user has 2 or fewer lives
            hintButton.setDisable(remainingAttempts <= 2);
        }
    }

    /**
     * Handles End-Of-Game logic (Win or Loss).
     * 1. Stops timer.
     * 2. Saves stats to DataHolder.
     * 3. Calculates score (if win).
     * 4. Delays scene switch to allow user to see the result briefly.
     */
    private void gameOver(boolean win) {
        timer.stop();
        disableAllKeys(); // Stop input

        // Save session stats to DataHolder so ScoreController can read them
        DataHolder.lastTime = timerLabel.getText().replace("Time passed: ", "");
        DataHolder.lastRemainingAttempts = remainingAttempts;

        if (!win) {
            // LOSS: Reveal the full word in RED
            StringBuilder revealed = new StringBuilder();
            for (char c : secretWord.toCharArray()) revealed.append(c).append(" ");
            wordDisplay.setText(revealed.toString().trim());
            wordDisplay.setStyle("-fx-fill: red; -fx-font-size: 48px; -fx-font-family: 'Verdana';");
        } else {
            // WIN: Show word in GREEN and calculate score
            wordDisplay.setStyle("-fx-fill: green; -fx-font-size: 48px; -fx-font-family: 'Verdana';");

            // Score Calculation Logic
            int basePoints = remainingAttempts * 1000;
            int multiplier = 1;
            // Higher difficulty = Higher score
            if (DataHolder.difficulty.equals("Medium")) multiplier = 2;
            if (DataHolder.difficulty.equals("Hard")) multiplier = 3;

            DataHolder.lastRoundScore = basePoints * multiplier;
            DataHolder.totalScore += DataHolder.lastRoundScore; // Add to session total
            DataHolder.wordsGuessed++;
        }

        DataHolder.isWin = win;

        // Create a 2-second delay before switching screens
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            // Switch scene to ScoreScreen
            Stage stage = (Stage) wordDisplay.getScene().getWindow();
            sceneSwitcher.switchTo("ScoreScreen", stage);
        });
        pause.play();
    }

    // Helper to disable all keyboard buttons at end of game
    private void disableAllKeys() {
        for (javafx.scene.Node rowNode : keyboardBox.getChildren()) {
            if (rowNode instanceof HBox) {
                for (javafx.scene.Node btnNode : ((HBox) rowNode).getChildren()) {
                    btnNode.setDisable(true);
                }
            }
        }
    }
}