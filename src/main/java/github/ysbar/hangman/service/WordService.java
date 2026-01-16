package github.ysbar.hangman.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @AUTHOR: Siqi
 * RESPONSIBILITY: Handles File I/O for word lists and random word selection.
 */

public class WordService {

    public String getRandomWord(String difficulty) {
        String filename = "";
        final String BASE_PATH = "/github/ysbar/hangman/";

        // Map difficulty string to filename
        filename = switch (difficulty) {
            case "Easy" -> "easy.csv";
            case "Hard" -> "hard.csv";
            default -> "medium.csv";
        };

        List<String> words = new ArrayList<>();

        // Try-with-resources ensures the InputStream is closed automatically
        try (InputStream is = getClass().getResourceAsStream(BASE_PATH + filename)) {

            // Fallback: try root path if package path fails
            InputStream finalStream = (is != null) ? is : getClass().getResourceAsStream("/" + filename);

            // ERROR HANDLING: File not found
            if (finalStream == null) {
                System.err.println("ERROR: Dictionary file not found: " + filename);
                return "DEVELOPER"; // Hardcoded fallback to prevent crash
            }

            // Stream API to read lines, trim whitespace, and ignore empty lines
            words = new BufferedReader(new InputStreamReader(finalStream))
                    .lines()
                    .map(String::trim)
                    .filter(w -> !w.isEmpty())
                    .toList();

            if (finalStream != is) finalStream.close();

        } catch (IOException e) {
            System.err.println("IO ERROR while reading word file: " + e.getMessage());
        }

        // ERROR HANDLING: Empty file
        if (words.isEmpty()) {
            return "JAVA";
        }

        // Return random word in Uppercase
        return words.get(new Random().nextInt(words.size())).toUpperCase();
    }
}