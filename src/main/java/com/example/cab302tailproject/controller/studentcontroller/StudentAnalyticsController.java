package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.StudentCardResponse;
import com.example.cab302tailproject.model.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.application.Platform;


import java.io.IOException;
import java.net.URL;
import java.util.List;

public class StudentAnalyticsController {

    @FXML private Button sidebarBackButton;
    @FXML private Button myPerformanceButton;
    @FXML private Button learningCardsButton;
    @FXML private Button aiAssistantButton;

    @FXML private Button homeButton;
    @FXML private Button settingsButton;

    @FXML private ComboBox<Integer> weekSelectionComboBox;
    @FXML private TableView<CardPerformanceEntry> cardPerformanceTableView;
    @FXML private TableColumn<CardPerformanceEntry, String> cardQuestionColumn;
    @FXML private TableColumn<CardPerformanceEntry, String> resultColumn;

    private ContentDAO contentDao;
    private SqlStudentDAO studentDao;
    private UserSession session;

    private ObservableList<CardPerformanceEntry> cardPerformanceData = FXCollections.observableArrayList();

    /**
     * Simple inner class to represent a row in the card performance table.
     */
    public static class CardPerformanceEntry {
        private final SimpleStringProperty cardQuestion;
        private final SimpleStringProperty result;

        public CardPerformanceEntry(String cardQuestion, String result) {
            this.cardQuestion = new SimpleStringProperty(cardQuestion);
            this.result = new SimpleStringProperty(result);
        }

        public String getCardQuestion() { return cardQuestion.get(); }
        public SimpleStringProperty cardQuestionProperty() { return cardQuestion; }
        public String getResult() { return result.get(); }
        public SimpleStringProperty resultProperty() { return result; }
    }

    @FXML
    public void initialize() {
        System.out.println("StudentAnalyticsController initializing...");
        try {
            contentDao = new ContentDAO();
            studentDao = new SqlStudentDAO();
            session = UserSession.getInstance();
        } catch (Exception e) {
            System.err.println("Error initializing DAOs or Session in StudentAnalyticsController: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Could not initialize data services. Please restart the application.");
            return;
        }


        if (cardQuestionColumn != null) {
            cardQuestionColumn.setCellValueFactory(new PropertyValueFactory<>("cardQuestion"));
        }
        if (resultColumn != null) {
            resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        }
        if (cardPerformanceTableView != null) {
            cardPerformanceTableView.setItems(cardPerformanceData);
            cardPerformanceTableView.setPlaceholder(new Label("Select a week to view performance.")); // Default placeholder
        }

        if (weekSelectionComboBox != null) {
            weekSelectionComboBox.setOnAction(event -> loadCardPerformanceForSelectedWeek());
            populateWeekComboBox();
        } else {
            System.err.println("weekSelectionComboBox is null. Check FXML fx:id.");
        }
        System.out.println("StudentAnalyticsController initialized.");
    }

    private void populateWeekComboBox() {
        System.out.println("Attempting to populate week ComboBox...");
        if (session == null || session.getEmail() == null) {
            System.err.println("No student logged in (session or email is null). Cannot populate weeks.");
            if (weekSelectionComboBox != null) {
                weekSelectionComboBox.setDisable(true);
                weekSelectionComboBox.setPromptText("Login required");
            }
            return;
        }
        System.out.println("Student email from session: " + session.getEmail());

        int studentId = studentDao.getStudentIDByEmail(session.getEmail());
        if (studentId == -1) {
            System.err.println("Could not find student ID for email: " + session.getEmail());
            if (weekSelectionComboBox != null) {
                weekSelectionComboBox.setDisable(true);
                weekSelectionComboBox.setPromptText("Student not found");
            }
            return;
        }
        System.out.println("Retrieved StudentID: " + studentId);

        int classroomId = studentDao.getClassroomIDForStudent(studentId);
        if (classroomId == -1) {
            System.err.println("Student " + studentId + " not assigned to any classroom. Cannot populate weeks.");
            if (weekSelectionComboBox != null) {
                weekSelectionComboBox.setDisable(true);
                weekSelectionComboBox.setPromptText("No classroom assigned");
            }
            return;
        }
        System.out.println("Retrieved ClassroomID: " + classroomId + " for StudentID: " + studentId);

        if (weekSelectionComboBox != null) {
            weekSelectionComboBox.setDisable(false);
            weekSelectionComboBox.setPromptText("Choose Week");
        }

        try {
            List<Integer> weeks = contentDao.getDistinctWeeksForClassroomLearningCards(classroomId);
            if (weeks != null && !weeks.isEmpty()) {
                System.out.println("Populating week ComboBox with weeks: " + weeks);
                if (weekSelectionComboBox != null) {
                    weekSelectionComboBox.setItems(FXCollections.observableArrayList(weeks));
                }
            } else {
                System.out.println("No distinct weeks found for classroom " + classroomId + " or weeks list is null.");
                if (weekSelectionComboBox != null) {
                    weekSelectionComboBox.setItems(FXCollections.observableArrayList());
                    weekSelectionComboBox.setPromptText("No learning card weeks");
                }
            }
        } catch (Exception e) {
            System.err.println("Error populating week ComboBox: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "Could not load available weeks.");
        }
    }

    private void loadCardPerformanceForSelectedWeek() {
        System.out.println("loadCardPerformanceForSelectedWeek called.");
        cardPerformanceData.clear();
        if (session == null || session.getEmail() == null || weekSelectionComboBox == null || weekSelectionComboBox.getValue() == null) {
            System.out.println("Cannot load card performance: session, email, or selected week is null.");
            if (cardPerformanceTableView != null) cardPerformanceTableView.setPlaceholder(new Label("Please select a student and a week."));
            return;
        }

        int studentId = studentDao.getStudentIDByEmail(session.getEmail());
        if (studentId == -1) {
            System.err.println("Cannot load card performance: Student ID not found for email " + session.getEmail());
            return;
        }

        int selectedWeek = weekSelectionComboBox.getValue();
        System.out.println("Loading performance for StudentID: " + studentId + ", Week: " + selectedWeek);
        int classroomId = studentDao.getClassroomIDForStudent(studentId);

        if (classroomId == -1) {
            System.err.println("Cannot load performance, classroom ID not found for student: " + studentId);
            if (cardPerformanceTableView != null) cardPerformanceTableView.setPlaceholder(new Label("Student not assigned to a classroom."));
            return;
        }
        System.out.println("Using ClassroomID: " + classroomId);

        try {
            List<StudentCardResponse> responses =
                    contentDao.getStudentCardResponsesForWeek(studentId, classroomId, selectedWeek);

            if (responses != null) {
                System.out.println("Fetched " + responses.size() + " responses.");
                for (StudentCardResponse response : responses) {
                    cardPerformanceData.add(new CardPerformanceEntry(
                            response.getCardQuestion(),
                            response.isCorrect() ? "Correct" : "Incorrect"
                    ));
                }
            } else {
                System.out.println("No responses fetched (responses list is null).");
            }

            if (cardPerformanceTableView != null) {
                if (cardPerformanceData.isEmpty()) {
                    cardPerformanceTableView.setPlaceholder(new Label("No responses found for week " + selectedWeek + "."));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading card performance data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "Could not load card performance data.");
        }
    }


    // --- Sidebar Navigation Handlers ---
    @FXML
    private void onSidebarBackClicked(ActionEvent event) throws IOException {
        loadScene(event, "ai_assistant-student.fxml", "TAIL - AI Assistant");
    }

    @FXML
    private void onMyPerformanceClicked(ActionEvent event) throws IOException {
        System.out.println("My Performance (Analytics) button clicked. Re-populating weeks.");
        populateWeekComboBox(); // Re-populate weeks, which might have been missed if student wasn't fully loaded initially
        cardPerformanceData.clear(); // Clear table
        if (cardPerformanceTableView != null) cardPerformanceTableView.setPlaceholder(new Label("Select a week to view performance."));

    }

    @FXML
    private void onLearningCardsClicked(ActionEvent event) throws IOException {
        loadScene(event, "learning_card-student.fxml", "TAIL - Learning Cards");
    }

    @FXML
    private void onAiAssistantClicked(ActionEvent event) throws IOException {
        loadScene(event, "ai_assistant-student.fxml", "TAIL - AI Assistant");
    }


    // --- Top Navigation Handlers ---
    @FXML
    private void onHomeClicked(ActionEvent event) throws IOException {
        loadScene(event, "student-dashboard.fxml", "TAIL - Student Dashboard");
    }

    @FXML
    private void onSettingsClicked(ActionEvent event) throws IOException {
        System.out.println("Settings button clicked - Student settings view to be implemented.");
        showAlert(Alert.AlertType.INFORMATION, "Not Implemented", "Settings page is under construction.");
    }


    // --- Utility Methods ---
    private void switchScene(ActionEvent event, String fxmlFileName, String windowTitle) {
        Stage stage;
        try {
            Node sourceNode = (Node) event.getSource();
            if (sourceNode == null || sourceNode.getScene() == null) {
                throw new IllegalStateException("Error switching scene: Could not get scene from the event source.");
            }
            stage = (Stage) sourceNode.getScene().getWindow();
            if (stage == null) {
                throw new IllegalStateException("Error switching scene: Could not get the stage.");
            }

            URL fxmlUrl = TailApplication.class.getResource(fxmlFileName);
            if (fxmlUrl == null) {
                if (!fxmlFileName.startsWith("/")) {
                    fxmlUrl = TailApplication.class.getResource("/" + fxmlFileName);
                }
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find FXML file: " + fxmlFileName + ". Ensure path is correct.");
                }
            }
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setTitle(windowTitle);
            stage.setScene(scene);
            System.out.println("Successfully switched scene to: " + fxmlFileName);

        } catch (IOException e) {
            System.err.println("IOException loading FXML (" + fxmlFileName + "): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested view: " + fxmlFileName + "\nReason: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error switching scene to " + fxmlFileName + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred.");
        }
    }

    private void loadScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        URL fxmlUrl = TailApplication.class.getResource(fxmlFile);

        if (fxmlUrl == null) {
            if (!fxmlFile.startsWith("/")) {
                fxmlUrl = TailApplication.class.getResource("/" + fxmlFile);
            }
            if (fxmlUrl == null) {
                String errorMessage = "Cannot find FXML file: " + fxmlFile +
                        ". Ensure the file exists and the path is correct (e.g., '/com/example/yourpackage/view.fxml' or 'view.fxml' if in the same package as TailApplication).";
                System.err.println(errorMessage);
                showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
                throw new IOException(errorMessage);
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setTitle(title);
        stage.setScene(scene);
        System.out.println("Successfully switched scene to: " + fxmlFile);
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
