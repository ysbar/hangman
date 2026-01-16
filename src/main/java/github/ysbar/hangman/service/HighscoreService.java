package github.ysbar.hangman.service;

import java.io.*;

/**
 * @AUTHOR: Siqi
 * RESPONSIBILITY: Manages persistence (Saving/Loading) of the highscore.
 */

public class HighscoreService {

    private static final String FILE_NAME = "highscore.csv";

    /**
     * Loads highscore from disk.
     * @return int array [Score, WordsGuessed]
     */
    public int[] loadHighscore() {
        int[] stats = {0, 0};
        File file = new File(FILE_NAME);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                if (line != null) {
                    // Expected Format: "SCORE;WORDS"
                    String[] parts = line.split(";");
                    if (parts.length >= 1) stats[0] = Integer.parseInt(parts[0].trim());
                    if (parts.length >= 2) stats[1] = Integer.parseInt(parts[1].trim());
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("ERROR: Could not read/parse highscore file.");
            }
        }
        return stats;
    }

    /**
     * Saves new highscore if better than current.
     */
    public void checkAndSaveHighscore(int newScore, int newWords) {
        int[] currentHigh = loadHighscore();

        if (newScore > currentHigh[0]) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
                bw.write(newScore + ";" + newWords);
                System.out.println("INFO: New highscore saved.");
            } catch (IOException e) {
                System.err.println("ERROR: Failed to write highscore.");
            }
        }
    }
}