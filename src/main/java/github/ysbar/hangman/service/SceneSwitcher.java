package github.ysbar.hangman.service;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @AUTHRO: Yusuf
 * RESPONSIBILITY: Encapsulates the logic for loading FXML files and switching scenes.
 */

public class SceneSwitcher {

    // Absolute path to resources ensures this works regardless of package depth
    private static final String BASE_PATH = "/github/ysbar/hangman/";

    /**
     * Switches scene triggered by a UI Event (e.g., Button Click).
     * Automatically retrieves the Stage from the event source.
     */
    public void switchTo(String fxmlFile, Event event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loadScene(fxmlFile, stage);
        } catch (Exception e) {
            System.err.println("Error: Could not retrieve Stage from Event.");
            e.printStackTrace();
        }
    }

    /**
     * Switches scene using a direct Stage reference (e.g., for Splash Screen or automated transitions).
     */
    public void switchTo(String fxmlFile, Stage stage) {
        loadScene(fxmlFile, stage);
    }

    // Internal helper to load the FXML and set the root
    private void loadScene(String fxmlFile, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_PATH + fxmlFile + ".fxml"));
            Parent root = loader.load();

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            // ERROR HANDLING: Critical for debugging missing files or typos
            System.err.println("CRITICAL ERROR: Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}