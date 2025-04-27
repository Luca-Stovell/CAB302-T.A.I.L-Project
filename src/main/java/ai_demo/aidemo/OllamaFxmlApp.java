package ai_demo.aidemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Main application class for the Ollama AI Chat.
 * Loads the FXML layout and starts the JavaFX application.
 */
public class OllamaFxmlApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Define the path to the FXML file within the resources folder
        // Make sure this path matches where you place the FXML file.
        String fxmlPath = "/ai_demo/aidemo/OllamaChatView.fxml";
        URL fxmlLocation = getClass().getResource(fxmlPath);

        // Check if the FXML file was found
        if (fxmlLocation == null) {
            System.err.println("Cannot find FXML file. Make sure it's in the correct resources path.");
            System.err.println("Expected path relative to resources root: " + fxmlPath);
            return; // Exit if FXML is not found
        }

        // Load the FXML file. This creates the UI graph and initializes the controller.
        Parent root = FXMLLoader.load(Objects.requireNonNull(fxmlLocation));

        primaryStage.setTitle("Ollama AI Chat");
        // Create the main scene with the loaded UI
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
