package com.example.cab302tailproject.utils;

import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Material;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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
        if (fxmlFile == null || fxmlFile.trim().isEmpty()) {
            throw new IllegalArgumentException("FXML file path cannot be null or empty.");
        }
        if (button == null || button.getScene() == null || button.getScene().getWindow() == null) {
            throw new IllegalArgumentException("Button or its associated stage cannot be null.");
        }

        Stage stage = (Stage) button.getScene().getWindow();
        URL resource = TailApplication.class.getResource(fxmlFile);
        if (resource == null) {
            throw new IOException("FXML file not found: " + fxmlFile +
                    ". Ensure the file exists and the path is correct.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource(fxmlFile));
        if (setScene) {     // If you want the window size to be reset
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            Parent root = fxmlLoader.load();
            stage.getScene().setRoot(root);
        }
    }


    /**
     * Navigates to a new content view by loading the specified FXML file, initialising its controller,
     * and replacing the current content in a given container. Optionally, the current view can be saved
     * before navigation.
     *
     * @param fxmlPath       The path to the FXML file to be loaded for the new view.
     * @param dynamicContent The container whose children will be replaced with the new view's content.
     * @param previousView   The container to store the current content, allowing it to be restored later.
     *                       Can be null if the current view does not need to be recalled (or if using an alternative
     *                       previousView initialisation).
     * @param currentMaterial The material object required for the new view. Included in this method to verify
     *                        that currentMaterial is set up correctly.
     * @param initController A functional interface for initialising the controller of the newly loaded FXML view
     *                       with the necessary attributes of a successful view change.
     *                       Use "(ExampleController controller) ->
     *                         controller.initData(currentMaterial, dynamicContentBox, previousView)"
     * @param <T>            The type of the controller associated with the new view.
     */
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
     * Used as a helper method for navigateToContent.
     *
     * @param <T> The type of the controller being initialized.
     */
    @FunctionalInterface
    public interface ControllerInitializer<T> {
        void initialize(T controller);
    }

    /**
     * Fetches the material content for a specific week, classroom, and material type.
     * If the material content is not found, displays an alert with appropriate messages.
     *
     * @param weekNumber The week number for which content is to be fetched.
     * @param classCheckBox A ChoiceBox containing the classroom ID. The selected classroom will be used to fetch the material.
     * @param materialType The type of the material to fetch (e.g., "Quiz", "Assignment").
     * @param contentDAO The data access object used to fetch material data from the database.
     * @return The material content if found, or null if no matching content exists or an error occurs.
     */
    public static Material fetchContent(
            int weekNumber,
            ChoiceBox<Integer> classCheckBox,
            String materialType,
            IContentDAO contentDAO) {

        if (classCheckBox == null) {
            throw new IllegalArgumentException("ChoiceBox is null.");
        }
        // Check if no value is selected
        if (classCheckBox.getValue() == null) {
            throw new IllegalArgumentException("No value selected in ChoiceBox.");
        }

        if (classCheckBox.getValue() != null) {
            try {
                // Retrieve classroom ID from the choice box
                int classroomID = classCheckBox.getValue();

                // Fetch material ID and content
                int materialID = contentDAO.getMaterialByWeekAndClassroom(weekNumber, materialType, classroomID);
                Material material = contentDAO.getMaterialContent(materialID, materialType);

                // Check for valid material content
                if (material.getContent() != null) {
                    return material; // Successfully found and fetched content
                } else {
                    // Show an alert if no content exists for the requested type
                    showAlert(Alert.AlertType.ERROR,
                            "No content found",
                            String.format("""
                                            A %s does not exist in week %d for classroom %d.\s
                                            \s
                                             \
                                            A teacher must generate the %s or assign it via 'Review' -> 'All content'.""",
                                    materialType, classroomID, weekNumber, materialType));
                    return null;
                }
            } catch (Exception e) {
                // Log and alert errors during retrieval
                System.err.printf(
                        "Error retrieving %s for classroom %d in week %d.%n",
                        materialType, classCheckBox.getValue(), weekNumber);
                showAlert(Alert.AlertType.ERROR,
                        "No content found",
                        String.format("""
                                        A %s does not exist in week %d for classroom %d.\s
                                        \s
                                         \
                                        A teacher must generate the %s or assign it via 'Review' -> 'All content'.""",
                                materialType, classCheckBox.getValue(), weekNumber, materialType));
                return null;
            }
        } else {
            // Alert when no classroom is selected
            showAlert(Alert.AlertType.WARNING,
                    "No classroom selected",
                    """
                            No classroom selected. Please select a classroom to view the content.\s
                            \s
                             \
                            If no classrooms are available, a teacher must assign one in their 'Students' dashboard.""");
            return null;
        }
    }





}
