module github.ysbar.hangman {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens github.ysbar.hangman.controller to javafx.fxml;
    exports github.ysbar.hangman;
    opens github.ysbar.hangman.model to javafx.fxml;
    opens github.ysbar.hangman.service to javafx.fxml;
}