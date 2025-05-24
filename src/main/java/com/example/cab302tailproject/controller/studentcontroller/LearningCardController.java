package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.LearningCards.LearningCardDeck;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.LearningCardCreator;
import com.example.cab302tailproject.model.StudentCardResponse;
import com.example.cab302tailproject.model.UserSession;
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
import java.util.ArrayList; // Keep this if LearningCardDeck constructor uses it
import java.util.List;

public class LearningCardController {

    @FXML private ComboBox<LearningCardCreator> cardList;
    @FXML private Button sidebarGenerateButton; // This seems to be from a different FXML context, ensure it's correct for this controller
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
    @FXML private Button nextButton; // Assuming this is a generic "next" if not using correct/incorrect
    @FXML private TextArea cardContent;

    @FXML private Button correctButton;
    @FXML private Button incorrectButton;


    private LearningCardDeck deck;
    private ContentDAO contentDAO;
    private SqlStudentDAO studentDAO;

    /**
     * Retrieves a deck from the database by its materialID.
     * @param materialId The MaterialID of the selected deck.
     */
    private void loadDeckByMaterialId(int materialId){
        if (contentDAO == null) {
            contentDAO = new ContentDAO(); // Ensure DAO is initialized
        }
        com.example.cab302tailproject.model.Material material = contentDAO.getMaterialContent(materialId, "learningCard");
        if (material != null && material.getContent() != null && !material.getContent().trim().isEmpty()) {
            deck = new LearningCardDeck(material.getContent());
            cardContent.setText(deck.getCurrentCard());
        } else {
            String errorMsg = "Error: Could not load deck content for MaterialID: " + materialId;
            if (material == null) errorMsg += " (Material not found).";
            else if (material.getContent() == null || material.getContent().trim().isEmpty()) errorMsg += " (Material content is empty).";

            cardContent.setText(errorMsg);
            System.err.println(errorMsg);
            deck = new LearningCardDeck(""); // Initialize with an empty deck to avoid NullPointerExceptions
        }
    }

    private ObservableList<LearningCardCreator> getDeckList(){
        if (contentDAO == null) {
            contentDAO = new ContentDAO();
        }
        return contentDAO.getAllCards();
    }

    @FXML public void initialize(){
        try {
            contentDAO = new ContentDAO();
            studentDAO = new SqlStudentDAO();
        } catch (Exception e) {
            System.err.println("Error initializing DAOs in LearningCardController: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Could not initialize data services.");
            return;
        }


        deck = new LearningCardDeck(new ArrayList<String[]>()); // Initialize with an empty list for the placeholder
        LearningCard placeholderCard = new LearningCard("Select a lesson card deck to begin", "(Use the dropdown menu on the right)");
        deck.addCardDirectly(placeholderCard); // Add a method to LearningCardDeck to add a card if needed for placeholders
        // Or, ensure the constructor LearningCardDeck(List<String[]>) handles empty list gracefully
        // and getCurrentCard() returns a default message.
        // For now, assuming getCurrentCard handles empty deck with EMPTY_MESSAGE
        cardContent.setText(deck.getCurrentCard());


        ObservableList<LearningCardCreator> options = getDeckList();
        if (options != null) {
            cardList.setItems(options);
        } else {
            cardList.setItems(FXCollections.observableArrayList());
            System.err.println("Warning: getAllCards returned null. ComboBox will be empty.");
        }


        cardList.setOnAction(event -> {
            LearningCardCreator selectedCardDeckInfo = cardList.getValue();
            if (selectedCardDeckInfo != null) {
                loadDeckByMaterialId(selectedCardDeckInfo.getMaterialID());
            }
        });
    }

    @FXML
    private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException {
        loadScene("ai_assistant-student.fxml");
    }

    @FXML
    private void onSidebarReviewClicked(ActionEvent event) throws IOException {
        loadScene("review-student.fxml");
    }

    @FXML
    private void onSidebarAnalysisClicked(ActionEvent event) throws IOException {
        loadScene("analytics-student.fxml");
    }

    @FXML
    private void onHomeClicked(ActionEvent event) throws IOException {
        loadScene("student-dashboard.fxml");
    }


    @FXML
    private void onFlipClicked(ActionEvent actionEvent) {
        if (deck != null && !deck.isEmpty()) {
            deck.flip();
            cardContent.setText(deck.getCurrentCard());
        } else {
            System.out.println("Flip clicked: Deck is null or empty.");
        }
    }

    // These methods (easy, hard, medium, again, next) might now be primarily driven by correct/incorrect
    // or could be kept for a different study mode.
    @FXML private void onEasyClicked(ActionEvent actionEvent) {
        // saveStudentResponse(true); // Decide if this should also save a response
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.easyNext());
        }
    }

    @FXML private void onHardClicked(ActionEvent actionEvent) {
        // saveStudentResponse(false); // Decide if this should also save a response
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.hardNext());
        }
    }

    @FXML private void onMediumClicked(ActionEvent actionEvent) {
        // saveStudentResponse(false); // Decide if this should also save a response
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.mediumNext());
        }
    }

    @FXML private void onAgainClicked(ActionEvent actionEvent) {
        // saveStudentResponse(false); // Decide if this should also save a response
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.hardNext());
        }
    }

    @FXML private void onNextClicked(ActionEvent actionEvent) {
        // This "Next" button is ambiguous without a correct/incorrect context.
        // It might be better to remove it if "Correct" and "Incorrect" are the primary interactions.
        // For now, let's assume it's like an "easy" pass.
        // saveStudentResponse(true);
        if (deck != null && !deck.isEmpty()) {
            cardContent.setText(deck.easyNext());
        }
    }

    @FXML
    private void onCorrectClicked(ActionEvent event) {
        System.out.println("Correct button clicked");
        if (deck == null || deck.isEmpty() || LearningCardDeck.EMPTY_MESSAGE.equals(deck.getCurrentCard())) {
            showAlert(Alert.AlertType.INFORMATION, "Deck Finished", "No more cards or no deck selected.");
            return;
        }
        saveStudentResponse(true);
        if (deck != null) { // Deck might become empty after saving and processing
            cardContent.setText(deck.easyNext()); // Use easyNext to advance after correct
        }
    }

    @FXML
    private void onIncorrectClicked(ActionEvent event) {
        System.out.println("Incorrect button clicked");
        if (deck == null || deck.isEmpty() || LearningCardDeck.EMPTY_MESSAGE.equals(deck.getCurrentCard())) {
            showAlert(Alert.AlertType.INFORMATION, "Deck Finished", "No more cards or no deck selected.");
            return;
        }
        saveStudentResponse(false);
        if (deck != null) { // Deck might become empty
            cardContent.setText(deck.hardNext()); // Use hardNext to advance after incorrect
        }
    }

    private void saveStudentResponse(boolean isCorrect) {
        UserSession session = UserSession.getInstance();
        String studentEmail = session.getEmail();
        LearningCardCreator selectedDeckInfo = cardList.getValue();

        if (studentEmail == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student logged in.");
            return;
        }
        // Check if deck is null or if the current card is the empty message BEFORE getting question
        if (deck == null || deck.isEmpty() || LearningCardDeck.EMPTY_MESSAGE.equals(deck.getCurrentCard())) {
            // This check is now also at the beginning of onCorrectClicked/onIncorrectClicked
            // showAlert(Alert.AlertType.WARNING, "No Card", "No card is currently displayed or deck is empty.");
            return;
        }
        if (selectedDeckInfo == null) {
            showAlert(Alert.AlertType.WARNING, "No Deck", "No deck selected.");
            return;
        }

        if (studentDAO == null) studentDAO = new SqlStudentDAO();
        if (contentDAO == null) contentDAO = new ContentDAO();

        int studentID = studentDAO.getStudentIDByEmail(studentEmail);
        if (studentID == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not find student ID for email: " + studentEmail);
            return;
        }
        int materialID = selectedDeckInfo.getMaterialID();

        String cardQuestion = deck.getCurrentQuestionText(); // Get the question text

        // This complex logic to get question if flipped might not be needed if getCurrentQuestionText always returns the question.
        // And if it's null, it means there's no card.
        if (cardQuestion == null || cardQuestion.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve current card question. Deck might be empty or card malformed.");
            return;
        }

        int classroomID = studentDAO.getClassroomIDForStudent(studentID);
        if (classroomID == -1) {
            System.out.println("Warning: Student " + studentEmail + " (ID: " + studentID + ") not associated with a classroom, or error fetching classroom. Saving response without classroom ID.");
        }

        boolean success = contentDAO.addStudentCardResponse(studentID, materialID, cardQuestion, isCorrect, classroomID);

        if (success) {
            System.out.println("Response saved: StudentID=" + studentID + ", MaterialID=" + materialID + ", Q=' " + (cardQuestion != null ? cardQuestion.substring(0, Math.min(cardQuestion.length(), 20)) : "N/A") +"...', Correct=" + isCorrect + ", ClassroomID=" + classroomID);
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save the response to the database.");
        }
    }


    private void loadScene(String fxmlFile) throws IOException {
        // Assuming homeButton is always present to get the scene/window
        if (homeButton == null || homeButton.getScene() == null) {
            System.err.println("loadScene: Cannot get scene from homeButton.");
            return;
        }
        Stage stage = (Stage) homeButton.getScene().getWindow();
        URL fxmlUrl = TailApplication.class.getResource(fxmlFile);
        if (fxmlUrl == null) {
            if (!fxmlFile.startsWith("/")) {
                fxmlUrl = TailApplication.class.getResource("/" + fxmlFile);
            }
            if (fxmlUrl == null) {
                String errorMessage = "Cannot find FXML file: " + fxmlFile;
                System.err.println(errorMessage);
                showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
                throw new IOException(errorMessage);
            }
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        // stage.setTitle(title); // Title was removed from this version of loadScene
        stage.setScene(scene);
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
