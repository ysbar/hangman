package github.ysbar.hangman.model;

/**
 * @AUTHOR: Siqi
 * RESPONSIBILITY:
 * Holds data that needs to persist between different scenes (Controllers).
 */

public class DataHolder {
    // --- Configuration Settings (Set in ConfigurationController) ---
    public static int targetAttempts = 6;
    public static String difficulty = "Medium";

    // --- Current Session Data (Modified in GameController) ---
    public static int totalScore = 0;
    public static int wordsGuessed = 0;
    public static int lastRoundScore = 0;
    public static boolean isWin = false;

    // --- Temporary Stats for Score Screen (Persisted after Game Over) ---
    public static String lastTime = "00:00";
    public static int lastRemainingAttempts = 0;

    /**
     * Resets the session data to default values.
     */
    public static void resetScore() {
        totalScore = 0;
        wordsGuessed = 0;
        lastRoundScore = 0;
    }
}