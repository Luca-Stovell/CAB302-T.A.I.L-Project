package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO; // For student classroom lookup
import com.example.cab302tailproject.LearningCards.LearningCardDeck;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.LearningCardCreator;
import com.example.cab302tailproject.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LearningCardController {

    @FXML private ComboBox<LearningCardCreator> cardList;
    @FXML private Button sidebarGenerateButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarAiAssistanceButton;
    @FXML private Button homeButton;
    @FXML private Button settingsButton;
    @FXML private Button flipButton;
    @FXML private Button easyButton;
    @FXML private Button hardButton;
    @FXML private Button againButton;
    @FXML private Button mediumButton;
    @FXML private Button nextButton;
    @FXML private TextArea cardContent;

    // FXML fields for Correct/Incorrect buttons
    @FXML private Button correctButton;
    @FXML private Button incorrectButton;


    private LearningCardDeck deck;
    private ContentDAO contentDAO;
    private SqlStudentDAO studentDAO;


    /**
     * Retrieves, from the database, a list of all learning card decks.
     * @return an ObservableList of LearningCardCreator objects.
     */
    private ObservableList<LearningCardCreator> getDeckList(){
        if (contentDAO == null) {
            contentDAO = new ContentDAO();
        }
        return contentDAO.getAllCards();
    }

    /**
     * Initializes the learning card page.
     * Loads the list of available decks into the ComboBox.
     * Sets up an initial empty/placeholder deck.
     * Defines the action for when a deck is selected from the ComboBox.
     */
    @FXML public void initialize(){
        contentDAO = new ContentDAO();
        studentDAO = new SqlStudentDAO();

        deck = new LearningCardDeck(new ArrayList<String[]>()
        {{
            add(new String[]{"Select a lesson card deck to begin", "(Use the dropdown menu on the right)"});
        }}
        );
        cardContent.setText(deck.getCurrentCard());

        try {
            ObservableList<LearningCardCreator> options = getDeckList();
            if (options == null) {
                // Handle case where getAllCards might return null
                options = FXCollections.observableArrayList();
                showAlert(Alert.AlertType.WARNING, "Load Warning", "Could not retrieve list of decks. List is empty.");
            }
            cardList.setItems(options);
        } catch (Exception e) {
            System.err.println("Error fetching deck list: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load deck list: " + e.getMessage());
            cardList.setItems(FXCollections.observableArrayList()); // Set empty list on error
        }


        cardList.setOnAction(event -> {
            LearningCardCreator selectedCardDeckInfo = cardList.getValue();
            if (selectedCardDeckInfo != null) {
                if (contentDAO == null) { // Should already be initialized
                    contentDAO = new ContentDAO();
                }
                com.example.cab302tailproject.model.Material material = contentDAO.getMaterialContent(selectedCardDeckInfo.getMaterialID(), "learningCard");
                if (material != null && material.getContent() != null && !material.getContent().trim().isEmpty()) {
                    deck = new LearningCardDeck(material.getContent());
                } else {
                    String errorMsg = "Error: Deck content not found or is empty for MaterialID: " + selectedCardDeckInfo.getMaterialID();
                    cardContent.setText(errorMsg);
                    System.err.println(errorMsg);
                    deck = new LearningCardDeck(""); // Fallback to empty deck
                }
                cardContent.setText(deck.getCurrentCard());
            }
        });
    }

    // --- Sidebar Navigation ---
    @FXML private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException { loadScene("ai_assistant-student.fxml"); }
    @FXML private void onSidebarReviewClicked(ActionEvent event) throws IOException { loadScene("review-student.fxml"); }
    @FXML private void onSidebarAnalysisClicked(ActionEvent event) throws IOException { loadScene("analytics-student.fxml"); }
    @FXML private void onHomeClicked(ActionEvent event) throws IOException { loadScene("student-dashboard.fxml"); }

    // --- Card Interaction Buttons ---
    @FXML
    private void onFlipClicked(ActionEvent actionEvent) {
        if (deck != null) {
            deck.flip();
            cardContent.setText(deck.getCurrentCard());
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Deck", "Please select a deck first.");
        }
    }

    // --- Spaced Repetition Buttons (Easy, Medium, Hard, Again) ---
    @FXML private void onEasyClicked(ActionEvent actionEvent) {
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.easyNext());
        } else {
            handleEmptyDeckOrNoSelection();
        }
    }

    @FXML private void onHardClicked(ActionEvent actionEvent) {
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.hardNext());
        } else {
            handleEmptyDeckOrNoSelection();
        }
    }

    @FXML private void onMediumClicked(ActionEvent actionEvent) {
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.mediumNext());
        } else {
            handleEmptyDeckOrNoSelection();
        }
    }

    @FXML private void onAgainClicked(ActionEvent actionEvent) { // "Again" often means "Hard"
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.hardNext());
        } else {
            handleEmptyDeckOrNoSelection();
        }
    }

    @FXML private void onNextClicked(ActionEvent actionEvent) { // "Next" often implies "Easy" or just advance
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.easyNext()); // Or a more generic advance if "Next" has different semantics
        } else {
            handleEmptyDeckOrNoSelection();
        }
    }

    // --- Correct / Incorrect Buttons ---
    @FXML
    private void onCorrectClicked(ActionEvent event) {
        System.out.println("Correct button clicked");
        if (deck == null || deck.isEmpty() || cardList.getValue() == null) {
            handleEmptyDeckOrNoSelection("Cannot mark 'Correct': No card or deck selected.");
            return;
        }
        saveStudentResponse(true); // Save response before advancing
        cardContent.setText(deck.processCorrectAndAdvance()); // Use new method
    }

    @FXML
    private void onIncorrectClicked(ActionEvent event) {
        System.out.println("Incorrect button clicked");
        if (deck == null || deck.isEmpty() || cardList.getValue() == null) {
            handleEmptyDeckOrNoSelection("Cannot mark 'Incorrect': No card or deck selected.");
            return;
        }
        saveStudentResponse(false); // Save response before advancing
        cardContent.setText(deck.processIncorrectAndAdvance()); // Use new method
    }

    private void handleEmptyDeckOrNoSelection(String... customMessage) {
        String message = (customMessage.length > 0 && customMessage[0] != null) ? customMessage[0] : "No card to process. Please select a deck or ensure the deck is not empty.";
        if (deck != null && !deck.isEmpty()) { // If deck exists but somehow became empty during operation
            cardContent.setText(LearningCardDeck.EMPTY_MESSAGE);
        } else { // No deck selected or deck was initially empty
            showAlert(Alert.AlertType.INFORMATION, "No Card", message);
            if (deck != null) cardContent.setText(deck.getCurrentCard()); // Show empty message if deck object exists
        }
    }


    /**
     * Saves the student's response (correct/incorrect) to the database.
     * @param isCorrect true if the student marked the card as correct, false otherwise.
     */
    private void saveStudentResponse(boolean isCorrect) {
        UserSession session = UserSession.getInstance();
        String studentEmail = session.getEmail(); // Assuming UserSession provides this
        LearningCardCreator selectedDeckInfo = cardList.getValue();

        // Validations
        if (studentEmail == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student logged in. Cannot save response.");
            return;
        }
        if (selectedDeckInfo == null) {
            showAlert(Alert.AlertType.WARNING, "No Deck Selected", "Cannot save response: No deck is selected.");
            return;
        }

        String cardQuestion = deck.getCurrentQuestionText();

        if (deck == null || deck.isEmpty() || cardQuestion == null || LearningCardDeck.EMPTY_MESSAGE.equals(deck.getCurrentCard())) {
            // Check if it's the placeholder card
            if (deck != null && deck.getCurrentCard().startsWith("Select a lesson card deck")) {
                showAlert(Alert.AlertType.WARNING, "No Card", "Please select a deck and start reviewing cards before marking correct/incorrect.");
                return;
            }
            showAlert(Alert.AlertType.WARNING, "No Card", "No card is currently displayed or deck is empty. Cannot save response.");
            return;
        }


        if (studentDAO == null) studentDAO = new SqlStudentDAO();
        if (contentDAO == null) contentDAO = new ContentDAO();

        int studentID = studentDAO.getStudentIDByEmail(studentEmail);
        if (studentID == -1) {
            showAlert(Alert.AlertType.ERROR, "Student ID Error", "Could not find student ID for email: " + studentEmail);
            return;
        }
        int materialID = selectedDeckInfo.getMaterialID();


        if (cardQuestion == null || cardQuestion.isEmpty() || LearningCardDeck.EMPTY_MESSAGE.equals(cardQuestion)) {
            showAlert(Alert.AlertType.ERROR, "Card Error", "Could not retrieve current card question text.");
            return;
        }

        int classroomID = studentDAO.getClassroomIDForStudent(studentID);
        if (classroomID == -1) {
            System.out.println("Warning: Student (ID: " + studentID + ") not associated with a classroom, or error fetching classroom. Saving response without classroom ID.");
        }

        boolean success = contentDAO.addStudentCardResponse(studentID, materialID, cardQuestion, isCorrect, classroomID);

        if (success) {
            System.out.println("Response saved: StudentID=" + studentID + ", MaterialID=" + materialID + ", Q='" + (cardQuestion.length() > 20 ? cardQuestion.substring(0, 20) + "..." : cardQuestion) +"', Correct=" + isCorrect + ", ClassroomID=" + (classroomID > 0 ? classroomID : "N/A"));
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save the response to the database.");
        }
    }

    /**
     * Loads a new FXML scene into the current stage.
     * @param fxmlFile The name of the FXML file to load (e.g., "student-dashboard.fxml").
     * @throws IOException If the FXML file cannot be loaded.
     */
    private void loadScene(String fxmlFile) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow(); // Get stage from any UI element
        FXMLLoader loader = new FXMLLoader(TailApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Utility method to show an alert dialog.
     * @param alertType The type of alert (e.g., Alert.AlertType.ERROR).
     * @param title The title of the alert window.
     * @param message The content message of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }
}
