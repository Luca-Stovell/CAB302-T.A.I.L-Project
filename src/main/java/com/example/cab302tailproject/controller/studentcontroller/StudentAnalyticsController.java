package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.StudentCardResponse; // Assuming this model exists
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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.example.cab302tailproject.utils.Alerts.showAlert;

public class StudentAnalyticsController {

    @FXML private Button sidebarBackButton; // For navigating back
    @FXML private Button myPerformanceButton; // Example sidebar button
    @FXML private Button learningCardsButton; // Example sidebar button
    @FXML private Button aiAssistantButton;   // Example sidebar button


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
            return; // Prevent further initialization if DAOs fail
        }


        if (cardQuestionColumn != null) {
            cardQuestionColumn.setCellValueFactory(new PropertyValueFactory<>("cardQuestion"));
        }
        if (resultColumn != null) {
            resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        }
        if (cardPerformanceTableView != null) {
            cardPerformanceTableView.setItems(cardPerformanceData);
        }

        if (weekSelectionComboBox != null) {
            weekSelectionComboBox.setOnAction(event -> loadCardPerformanceForSelectedWeek());
            populateWeekComboBox();
        }

        System.out.println("StudentAnalyticsController initialized.");
    }

    private void populateWeekComboBox() {
        if (session == null || session.getEmail() == null) {
            System.err.println("No student logged in. Cannot populate weeks.");
            weekSelectionComboBox.setDisable(true);
            weekSelectionComboBox.setPromptText("Login required");
            return;
        }

        int studentId = studentDao.getStudentIDByEmail(session.getEmail());
        if (studentId == -1) {
            System.err.println("Could not find student ID for email: " + session.getEmail());
            weekSelectionComboBox.setDisable(true);
            weekSelectionComboBox.setPromptText("Student not found");
            return;
        }

        int classroomId = studentDao.getClassroomIDForStudent(studentId);
        if (classroomId == -1) {
            System.err.println("Student not assigned to any classroom. Cannot populate weeks.");
            weekSelectionComboBox.setDisable(true);
            weekSelectionComboBox.setPromptText("No classroom assigned");
            return;
        }

        weekSelectionComboBox.setDisable(false);
        weekSelectionComboBox.setPromptText("Choose Week");

        try {
            List<Integer> weeks = contentDao.getDistinctWeeksForClassroomLearningCards(classroomId);
            if (weeks != null && !weeks.isEmpty()) {
                weekSelectionComboBox.setItems(FXCollections.observableArrayList(weeks));
            } else {
                weekSelectionComboBox.setItems(FXCollections.observableArrayList()); // Clear previous items
                weekSelectionComboBox.setPromptText("No learning card weeks");
            }
        } catch (Exception e) {
            System.err.println("Error populating week ComboBox: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "Could not load available weeks.");
        }
    }

    private void loadCardPerformanceForSelectedWeek() {
        cardPerformanceData.clear();
        if (session == null || session.getEmail() == null || weekSelectionComboBox.getValue() == null) {
            return;
        }

        int studentId = studentDao.getStudentIDByEmail(session.getEmail());
        if (studentId == -1) return; // Already handled by populateWeekComboBox checks

        int selectedWeek = weekSelectionComboBox.getValue();
        int classroomId = studentDao.getClassroomIDForStudent(studentId);

        if (classroomId == -1) {
            System.err.println("Cannot load performance, classroom ID not found for student.");
            return;
        }

        try {
            List<StudentCardResponse> responses =
                    contentDao.getStudentCardResponsesForWeek(studentId, classroomId, selectedWeek);

            if (responses != null) {
                for (StudentCardResponse response : responses) {
                    cardPerformanceData.add(new CardPerformanceEntry(
                            response.getCardQuestion(),
                            response.isCorrect() ? "Correct" : "Incorrect"
                    ));
                }
                System.out.println("Loaded " + responses.size() + " card responses for student " + studentId + ", week " + selectedWeek);
            }
            if (cardPerformanceTableView.getItems().isEmpty()) {
                cardPerformanceTableView.setPlaceholder(new Label("No responses found for week " + selectedWeek + "."));
            } else {
                cardPerformanceTableView.setPlaceholder(new Label("Select a week to view performance.")); // Default placeholder
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
        // Navigate to the previous relevant student screen, e.g., student dashboard
        loadScene(event, "learning_cards.fxml", "TAIL - Student Dashboard");
    }

    @FXML
    private void onMyPerformanceClicked(ActionEvent event) throws IOException {
        // Already on this page or refresh
        initialize(); // Re-initialize to refresh data
        System.out.println("My Performance (Analytics) button clicked.");
    }

    @FXML
    private void onLearningCardsClicked(ActionEvent event) throws IOException {
        loadScene(event, "learning_card-student.fxml", "TAIL - Learning Cards");
    }

    @FXML
    private void onAiAssistantClicked(ActionEvent event) throws IOException {
        loadScene(event, "ai_assistant-student.fxml", "TAIL - AI Assistant");
    }


    // Updated loadScene method to be more robust
    // TODO: replace this with utils method. Utils method has borrowed some of your null checks - title change is probably not needed
    private void loadScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        URL fxmlUrl = TailApplication.class.getResource(fxmlFile);

        if (fxmlUrl == null) {
            // Try with an absolute path from the classpath root if the relative one failed
            if (!fxmlFile.startsWith("/")) {
                fxmlUrl = TailApplication.class.getResource("/" + fxmlFile);
            }
            if (fxmlUrl == null) {
                String errorMessage = "Cannot find FXML file: " + fxmlFile +
                        ". Ensure the file exists and the path is correct (e.g., '/com/example/yourpackage/view.fxml' or 'view.fxml' if in the same package as TailApplication).";
                System.err.println(errorMessage);
                showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
                throw new IOException(errorMessage); // Propagate the error to be caught by JavaFX if not handled by caller
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setTitle(title);
        stage.setScene(scene);
        System.out.println("Successfully switched scene to: " + fxmlFile);
    }
}
