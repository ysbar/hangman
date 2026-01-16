package github.ysbar.hangman;

import github.ysbar.hangman.service.SceneSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * MAIN ENTRY POINT
 * Responsibilities:
 * 1. Initializes the JavaFX Application.
 * 2. Sets up the primary Stage (Window) properties.
 * 3. Delegates the initial view loading to SceneSwitcher.
 */

public class Launcher extends Application {

    // Service to handle scene transitions
    private SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hangman");

        // UX: Enforce minimum window size to prevent layout breakage
        stage.setMinWidth(900);
        stage.setMinHeight(700);

        // Start the application flow with the Splash Screen
        sceneSwitcher.switchTo("SplashScreen", stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}