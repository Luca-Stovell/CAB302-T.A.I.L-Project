package com.example.cab302tailproject.utils;

import com.example.cab302tailproject.TailApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.cab302tailproject.utils.Alerts.showAlert;

public class SceneHandling {
    /**
     * Loads a new scene from the specified FXML file into the given stage.
     * Provides the option to replace the entire scene or just update the scene's root.
     *
     * @param fxmlFile The path to the FXML file to be loaded.
     * @param button The button whose associated stage will be used for the scene transition.
     * @param setScene Determines whether the scene is replaced entirely (true),
     *                 or only the root node of the current scene is replaced (false).
     *                 Set to false if the view can handle different sizes.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public static void loadScene(String fxmlFile, Button button, boolean setScene) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource(fxmlFile));
        if (setScene) {     // If you want the window size to be reset
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            Parent root = fxmlLoader.load();
            stage.getScene().setRoot(root);
        }
    }


    public static <T> void navigateToContent(String fxmlPath,
                                             VBox dynamicContent,
                                             VBox previousView,
                                             Object currentMaterial,
                                             ControllerInitializer<T> initController) {
        try {
            // Check if currentMaterial is null
            if (currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Navigation Error", "Current material data is missing.");
                return;
            }
            // Allow previous view to not be saved (simply passed in 5th parameter) if null
            if (previousView != null) {
                previousView.getChildren().setAll(dynamicContent.getChildren());
            }

            // Load new scene
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource(fxmlPath));
            VBox newContent = fxmlLoader.load();

            // Initialize controller
            T controller = fxmlLoader.getController();
            initController.initialize(controller);

            // Replace content in the dynamic container
            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(newContent);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load the specified view.\n" + e.getMessage());
        }
    }

    /**
     * A functional interface for initializing controllers.
     *
     * @param <T> The type of the controller being initialized.
     */
    @FunctionalInterface
    public interface ControllerInitializer<T> {
        void initialize(T controller);
    }

}
