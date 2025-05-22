package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.SqliteClassroomDAO;
import com.example.cab302tailproject.DAO.SqliteTeacherDAO;
import com.example.cab302tailproject.model.*;
import com.example.cab302tailproject.ollama4j.OllamaSyncResponse;

import com.example.cab302tailproject.TailApplication;
import io.github.ollama4j.exceptions.OllamaBaseException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.util.List;

import java.io.File;
import java.io.IOException;


/**
 * Controller for the Teacher's Lesson Generation view.
 * Handles user input for lesson/worksheet/flashcard generation, interacts with the Ollama service,
 * prompts for saving content, and manages navigation to other views by directly switching
 * scenes on the current stage within each action handler.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.12
 */
public class LessonGenController {
    //<editor-fold desc="Field declarations">
    //<editor-fold desc="FXML UI Element References - Main Content">

    @FXML private Label loggedInTeacherLabel;
    /**
     * TextField for user input to generate lessons or worksheets.
     */
    @FXML private TextField generatorTextField;
    /**
     * Button to trigger the generation process.
     */
    @FXML private Button generateButton;
    /**
     * RadioButton option for generating a worksheet.
     */
    @FXML private RadioButton worksheetRadioButton;
    /**
     * RadioButton option for generating a lesson plan.
     */
    @FXML private RadioButton lessonPlanRadioButton;
    /**
     * RadioButton option for generating flash cards.
     */
    @FXML private RadioButton flashCardsRadioButton; // Added
    /**
     * ToggleGroup for the worksheet, lesson plan, and flash cards radio buttons.
     */
    @FXML private ToggleGroup generateToggleGroup;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Navigation & Layout">
    /**
     * Button for navigating to a "Files" section.
     */
    @FXML private Button filesButton;
    /**
     * Button for navigating to a "Students" section.
     */
    @FXML private Button studentsButton;
    /**
     * Button for navigating to a "Home" or main dashboard.
     */
    @FXML private Button homeButton;
    /**
     * Button for navigating to "Settings".
     */
    @FXML private Button settingsButton;

    // Sidebar buttons - @FXML fields are optional if only onAction is used.
    /**
     * Button in the sidebar to navigate to the Library view.
     */
    @FXML private Button sidebarLibraryButton;
    /**
     * Button in the sidebar for generating content.
     */
    @FXML private Button sidebarGenerateButton;
    /**
     * Button in the sidebar for reviewing content.
     */
    @FXML private Button sidebarReviewButton;
    /**
     * Button in the sidebar for analysis.
     */
    @FXML private Button sidebarAnalysisButton;
    /**
     * Button in the sidebar for AI assistance.
     */
    @FXML private Button sidebarAiAssistanceButton;


    //</editor-fold>

    //<editor-fold desc="Other Fields">

    /**
     * Represents the identifier of the currently selected or generated material.
     */
    private Material currentMaterial;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Dynamic content">
    /**
     * A reference to a VBox in the JavaFX view.
     * Represents a dynamic content container in the user interface, allowing the content
     * within it to be programmatically changed at runtime.
     * This element is used in the context of lesson or worksheet generation and navigation,
     * enabling the controller to update or switch the displayed content as required based
     * on user interaction or application logic.
     */
    @FXML
    private VBox dynamicContentBox;  // to change the content view


    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Initialisation">
    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the radio button toggle group and the file chooser.
     */
    @FXML
    public void initialize() {
        loggedInTeacherLabel.setText(UserSession.getInstance().getFullName());
        System.out.println("LessonGenController initializing (direct scene switching)...");
        if (generateToggleGroup == null || worksheetRadioButton == null || lessonPlanRadioButton == null || flashCardsRadioButton == null) {
            System.err.println("WARN: One or more generation UI elements (ToggleGroup, RadioButtons) are null. Check FXML fx:id connections.");
            if (generateToggleGroup == null) generateToggleGroup = new ToggleGroup();
        }
        if (worksheetRadioButton != null) {
            worksheetRadioButton.setToggleGroup(generateToggleGroup);
            worksheetRadioButton.setSelected(true); // Default selection
        }
        if (lessonPlanRadioButton != null) {
            lessonPlanRadioButton.setToggleGroup(generateToggleGroup);
        }
        if (flashCardsRadioButton != null) { // Added setup for flashCardsRadioButton
            flashCardsRadioButton.setToggleGroup(generateToggleGroup);
        }


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Generated Content");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        System.out.println("LessonGenController initialized.");
    }
    //</editor-fold>

    //<editor-fold desc="AI Content Generation Logic">
    /**
     * Handles the action event when the "Generate" button (for lessons/worksheets/flashcards) is clicked.
     * Validates input, constructs a prompt, runs the Ollama generation
     * in a background task, and handles success (saving file) or failure (alert).
     */
    @FXML
    private void onGenerateClicked() {
        System.out.println("Generate button clicked.");
        if (generateToggleGroup == null || generatorTextField == null || generateButton == null ||
                worksheetRadioButton == null || lessonPlanRadioButton == null || flashCardsRadioButton == null) {
            showAlert(Alert.AlertType.ERROR, "UI Error", "Core generation UI elements are not initialized properly. Check FXML connections.");
            return;
        }

        Toggle selectedToggle = generateToggleGroup.getSelectedToggle();
        String userInput = generatorTextField.getText();

        if (selectedToggle == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select 'Worksheet', 'Lesson Plan', or 'Flash Cards'.");
            return;
        }
        String selectedGeneratorType = ((RadioButton) selectedToggle).getText(); // e.g., "Worksheet", "Lesson Plan", "Flash Cards"

        if (userInput == null || userInput.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Missing", "Please enter a topic or description.");
            generatorTextField.requestFocus();
            return;
        }

        String prompt;
        if ("Flash Cards".equals(selectedGeneratorType)) {
            prompt = String.format("Generate exactly 10 flashcards on the topic of: %s. For each flashcard, provide the question on one line, and the answer on the very next line. Separate each question-answer pair (flashcard) from the next with a single blank line. The entire response should consist only of these 10 flashcards in this format, with no other introductory or concluding text. Example for two flashcards:\nWhat is the capital of France?\nParis\n\nWhat is 2 + 2?\n4. DO NOT GIVE ANY OTHER TEXT THEN THE QUESTION AND THE ANSWER", userInput.trim());
        } else {
            prompt = String.format("Generate a %s based on the following topic: %s",
                    selectedGeneratorType, userInput.trim());
        }

        System.out.println("Using prompt: " + prompt); // Log the prompt

        generateButton.setDisable(true);
        generatorTextField.setDisable(true);

        Task<String> generateTask = new Task<>() {
            @Override
            protected String call() throws OllamaBaseException, IOException, InterruptedException {
                OllamaSyncResponse service = new OllamaSyncResponse(prompt);
                return service.ollamaResponse();
            }
        };
        generateTask.setOnSucceeded(workerStateEvent -> {
            String generatedContent = generateTask.getValue();
            System.out.println("AI Generated Content (Raw):\n" + generatedContent);

            String contentToSave = generatedContent;
            if ("Flash Cards".equals(selectedGeneratorType)) {
                // For flashcards, trim the whole block but preserve internal newlines.
                contentToSave = generatedContent.trim();
                System.out.println("AI Generated Content (Trimmed for Flashcards):\n" + contentToSave);
            }


            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
            String generatorTypeForDB = typeFormatter(selectedGeneratorType);
            if ("INVALID".equals(generatorTypeForDB)) {
                showAlert(Alert.AlertType.ERROR, "Type Error", "Invalid generator type selected: " + selectedGeneratorType);
                return;
            }

            int generatedID = saveLessonToDatabase(contentToSave, generatorTypeForDB, userInput.trim());

            if (generatedID != -1) {
                currentMaterial = new Material(generatedID, generatorTypeForDB);
                navigateToGeneratedPlan(generatedID);
            } else {
                showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save the generated content to the database.");
            }
        });
        generateTask.setOnFailed(workerStateEvent -> {
            Throwable exception = generateTask.getException();
            System.err.println("Ollama generation task failed: " + exception.getClass().getName() + " - " + exception.getMessage());
            String errorMessage = "Could not generate content.";
            if (exception instanceof IOException) errorMessage += " Network/server issue.";
            else if (exception instanceof OllamaBaseException) errorMessage += " AI API error.";
            else if (exception instanceof InterruptedException) {
                errorMessage += " Process interrupted.";
                Thread.currentThread().interrupt();
            }
            showAlert(Alert.AlertType.ERROR, "Generation Failed", errorMessage + "\nDetails: " + exception.getMessage());
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
        });
        new Thread(generateTask).start();
    }

    /**
     * Saves the supplied content to the database. Assumes the teacher and classroom.
     * @param content The full text of content to be added
     * @param type The type of material it is (e.g., "lesson", "worksheet", "learningCard").
     * @param topic The topic of the content. Generally the AI prompt entered to create the content.
     */
    private int saveLessonToDatabase(String content, String type, String topic) {
        if (content == null) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save null content.");
            return -1;
        }
        if ("INVALID".equals(type)) { // Check against the "INVALID" string from typeFormatter
            System.err.println("Invalid content type for DB: " + type + ".");
            showAlert(Alert.AlertType.ERROR, "Save Error", "Invalid content type specified for saving.");
            return -1;
        }


        // Limit topic length
        if (topic.length() > 50) topic = topic.substring(0, 50);
        String teacherEmail = getTeacherEmailFromSession();

        try {
            IContentDAO contentDAO = new ContentDAO();
            int teacherID = contentDAO.getTeacherID(teacherEmail);
            if (teacherID == -1 && teacherEmail != null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Teacher email " + teacherEmail + " not found in database.");
                return -1;
            } else if (teacherID == -1 && teacherEmail == null) {
                showAlert(Alert.AlertType.ERROR, "Session Error", "No teacher logged in. Cannot save content.");
                return -1;
            }


            Material materialContent = new Material(topic, content, teacherID, type);
            int generatedMaterialID = contentDAO.addContent(materialContent, type);

            if (generatedMaterialID != -1) {
                boolean classroomSaved = assignClassroomAndWeekToContent(generatedMaterialID);
                if (!classroomSaved) {
                    System.err.println("Failed to assign week and classroom to the content with MaterialID: " + generatedMaterialID);
                }
                return generatedMaterialID;
            }
            else {
                System.err.println("Failed to save lesson to the database. addContent returned -1.");
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save content to the database.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while saving the content: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "An unexpected error occurred while saving: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Assigns a week number and classroom ID to the specified material in the database.
     * The method determines the appropriate week and classroom based on the current
     * teacher's session information. If the teacher information is unavailable, default
     * values are used to update the material.
     *
     * @param materialID The unique identifier of the material to which a week and classroom
     * are to be assigned. Must not be -1.
     * @return true if the operation to assign week and classroom to the material is successful,
     * false otherwise.
     */
    public boolean assignClassroomAndWeekToContent(int materialID) {
        if (materialID != -1) {
            String teacherEmail = getTeacherEmailFromSession();
            int teacherClassroomId = 0;
            int assignedWeek = 0;

            try {
                IContentDAO contentDAO = new ContentDAO();
                if (teacherEmail == null) {
                    System.err.println("Assigning default classroom/week as no teacher details found for MaterialID: " + materialID);
                    contentDAO.updateClassroomID(teacherClassroomId, materialID);
                    contentDAO.updateWeek(assignedWeek, materialID);
                    System.out.println("Placeholder classroom and week saved for MaterialID: " + materialID);
                } else {
                    SqliteClassroomDAO classroomDAO = new SqliteClassroomDAO();
                    List<Classroom> classrooms = classroomDAO.getClassroomsByTeacherEmail(teacherEmail);
                    if (classrooms != null && !classrooms.isEmpty()) {
                        teacherClassroomId = (classrooms.getLast().getClassroomID());
                    } else {
                        System.err.println("No classrooms found for teacher: " + teacherEmail + ". Using default classroom ID 0 for MaterialID: " + materialID);
                        showAlert(Alert.AlertType.WARNING, "Classroom Assignment", "No classrooms found for your account. The content was saved without a specific classroom assignment.");
                    }
                    contentDAO.updateClassroomID(teacherClassroomId, materialID);
                    contentDAO.updateWeek(assignedWeek, materialID);
                    System.out.println("Classroom ID " + teacherClassroomId + " and placeholder week " + assignedWeek + " saved successfully for MaterialID: " + materialID);
                }
                return true;
            }
            catch (Exception e) {
                System.err.println("An error occurred while saving the week and class for MaterialID " + materialID + ": " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Assignment Error","Failed to assign a classroom to the content for MaterialID: " + materialID + ".\nError: " + e.getMessage());
            }
        } else {
            System.err.println("Cannot assign classroom/week to invalid MaterialID: " + materialID);
        }
        return false;
    }

    /**
     * Retrieves the email address of the currently logged-in teacher from the user session.
     *
     * @return The email address of the currently logged-in teacher, or null if no teacher is logged in.
     */
    private String getTeacherEmailFromSession() {
        UserSession userSession = UserSession.getInstance();
        String teacherEmail = userSession.getEmail();
        if (teacherEmail == null) {
            System.err.println("No logged in teacher found in session.");
            return null;
        } else {
            return teacherEmail;
        }
    }

    /**
     * Formats the given type string by mapping specific types to their database-compatible values.
     * Only use in specific cases.
     *
     * @param type The type of material to be formatted. Expected values include "Lesson Plan",
     * "Worksheet", and "Flash Cards". Other strings will be returned as "INVALID".
     * @return A formatted string representation of the input type for database use.
     * For "Lesson Plan", returns "lesson". For "Worksheet", returns "worksheet".
     * For "Flash Cards", returns "learningCard". If no match, it returns "INVALID".
     */
    private String typeFormatter(String type) {
        return switch (type) {
            case "Lesson Plan" -> "lesson";
            case "Worksheet" -> "worksheet";
            case "Flash Cards" -> "learningCard";
            default -> {
                System.err.println("Unrecognized generator type for formatting: " + type);
                yield "INVALID";
            }
        };
    }

    //</editor-fold>

    //<editor-fold desc="Page navigation">
    /**
     * Navigates to the generated lesson plan view for the material specified by the given ID.
     * If no material is found with the provided ID, displays a warning alert.
     * Saves the current view to allow navigating back later and handles the transition
     * to the new lesson plan view with the relevant data.
     *
     * @param materialID The identifier of the material for which the lesson plan is to be generated.
     */
    private void navigateToGeneratedPlan(int materialID) {
        try {
            if (this.currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Navigation Error", "Current material data is missing for ID: " + materialID + ".");
                return;
            }

            VBox previousView = new VBox();
            previousView.getChildren().setAll(dynamicContentBox.getChildren());

            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("lesson_plan-teacher.fxml"));
            VBox layout = fxmlLoader.load();
            LessonPlanController controller = fxmlLoader.getController();
            controller.initData(currentMaterial, dynamicContentBox, previousView);

            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().add(layout);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load generated content view.\n" +e.getMessage());
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Sidebar Navigation Event Handlers - Direct Scene Switching">
    /**
     * Handles clicks on the "Generate" button in the sidebar.
     * Reloads the lesson generation view on the current stage.
     */
    @FXML
    private void onSidebarGenerateClicked() throws IOException {
        Stage stage = (Stage) sidebarGenerateButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("lesson_generator-teacher.fxml"));
        Parent root = fxmlLoader.load();
        stage.getScene().setRoot(root);
    }

    /**
     * Handles clicks on the "Review" button in the sidebar.
     * Loads the teacher review view on the current stage.
     */
    @FXML
    private void onSidebarReviewClicked() throws IOException {
        Stage stage = (Stage) sidebarReviewButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher.fxml"));
        Parent root = fxmlLoader.load();
        stage.getScene().setRoot(root);
    }

    /**
     * Handles clicks on the "Analysis" button in the sidebar.
     * Loads the teacher analysis view on the current stage.
     */
    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        Stage stage = (Stage) sidebarAnalysisButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("analytics-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "A.I. Assistance" button in the sidebar.
     * Loads the teacher AI assistance view on the current stage.
     */
    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        Stage stage = (Stage) sidebarAiAssistanceButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("ai_assistant-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "Library" button in the sidebar.
     * Loads the teacher library view on the current stage.
     */
    @FXML
    private void onSidebarLibraryClicked() throws IOException {
        Stage stage = (Stage) sidebarLibraryButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("library-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
    //</editor-fold>

    //<editor-fold desc="Top Navigation Event Handlers - Direct Scene Switching">
    /**
     * Handles clicks on the "Files" button in the top navigation.
     * Loads a files view on the current stage.
     */
    @FXML
    private void onFilesClicked() {
        System.out.println("Files button clicked.");
        // TODO ADD FUNCTIONALITY
    }

    /**
     * Handles clicks on the "Students" button in the top navigation.
     * Loads a students view on the current stage.
     */
    @FXML
    private void onStudentsClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("classroom-teacher-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        Stage stage = (Stage) studentsButton.getScene().getWindow();
        stage.setScene(scene);
    }


    /**
     * Handles clicks on the "Home" button in the top navigation.
     * Loads the main dashboard/home view on the current stage.
     */
    @FXML
    private void onHomeClicked() {
        System.out.println("Home button clicked.");
        // TODO ADD HOME BUTTON FUNCTIONALITY
    }

    /**
     * Handles clicks on the "Settings" button in the top navigation.
     * Loads a settings view on the current stage.
     */
    @FXML
    private void onSettingsClicked() {
        System.out.println("Settings button clicked.");
        // TODO ADD SETTINGS FUNCTIONALITY
    }
    //</editor-fold>

    //<editor-fold desc="Utility Methods">

    /**
     * Helper method to display a standard JavaFX Alert dialog.
     * Ensures the alert is shown on the JavaFX Application Thread.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert window.
     * @param message   The main message content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    /**
     * Internal helper method to create and show an alert.
     * This method should only be called from the JavaFX Application Thread.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert.
     * @param message   The content message of the alert.
     */
    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //</editor-fold>
}
