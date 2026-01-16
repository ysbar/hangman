# Hangman

---

A Hangman game developed by **AliJava**. This project demonstrates clean software architecture using the **MVC Pattern** and **JavaFX**.

## Architecture

* **`model`**: Manages session state (`DataHolder`).
* **`view`**: Defines the UI (`.fxml` & `styles.css`).
* **`controller`**: Handles user input and UI updates.
* **`service`**: Handles business logic (`WordService`, `HighscoreService`, `SceneSwitcher`).

## Setup & Run

1. Ensure **Java 17+** and **JavaFX** are installed.
3. Run `Launcher.java` to start.