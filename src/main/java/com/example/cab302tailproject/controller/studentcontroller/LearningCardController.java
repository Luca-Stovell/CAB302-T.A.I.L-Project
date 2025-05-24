package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO; // For student classroom lookup
import com.example.cab302tailproject.LearningCards.LearningCardDeck;
import com.example.cab302tailproject.model.LearningCardCreator;
import com.example.cab302tailproject.model.UserSession; // Assuming UserSession exists
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;
import static com.example.cab302tailproject.utils.TextFormatting.bindTimeToLabel;

public class LearningCardController {

    @FXML private Button logoutButton;
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

    // New FXML fields for Correct/Incorrect buttons
    @FXML private Button correctButton;
    @FXML private Button incorrectButton;


    private LearningCardDeck deck;
    private ContentDAO contentDAO;
    private SqlStudentDAO studentDAO; // To get classroom ID for student

    //<editor-fold desc="FXML UI Element References - Dynamic content">
    /**
     * This Label represents the UI element that displays the currently logged-in user's name.
     */
    @FXML
    Label LoggedInName;

    /**
     * Represents the JavaFX Label used to display the current time.
     */
    @FXML
    private Label timeLabel;
    //</editor-fold>

    /**
     * Retrieves a deck from the database by id, and stored it in deck
     * @param ID Database's materialID of the selected deck
     */
    private void getDeck(int ID){
        if (cardList.getValue() != null) {
            // Assuming LearningCardCreator's getMaterialID() IS the MaterialID for the deck.
            com.example.cab302tailproject.model.Material material = contentDAO.getMaterialContent(cardList.getValue().getMaterialID(), "learningCard");
            if (material != null) {
                deck = new LearningCardDeck(material.getContent());
                cardContent.setText(deck.getCurrentCard());
            } else {
                cardContent.setText("Error: Could not load deck content.");
                deck = new LearningCardDeck(""); // Empty deck
            }
        } else {
            cardContent.setText("Please select a deck.");
            deck = new LearningCardDeck(""); // Empty deck
        }
    }

    /**
     * Retrieves, from the database, a list of all learning card decks
     * @return a list of topics and ID of the decks
     */
    private ObservableList<LearningCardCreator> getDeckList(){
        // Ensure contentDAO is initialized before use
        if (contentDAO == null) {
            contentDAO = new ContentDAO();
        }
        return contentDAO.getAllCards();
    }

    /**
     * Initialises learning card page by loading in the deck and setting the initial display.
     * Also finds the list of learning cards, populates the comboBox with it, and defines onAction handling for the comboBox
     */
    @FXML public void initialize(){
        contentDAO = new ContentDAO(); // This line triggers the ContentDAO constructor and the table creation errors
        studentDAO = new SqlStudentDAO();
        LoggedInName.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");

        deck = new LearningCardDeck(new ArrayList<>()
        {{
            add(new String[]{"Select a lesson card deck to begin", "(Use the dropdown menu on the right)"});
        }}
        );
        cardContent.setText(deck.getCurrentCard());

        ObservableList<LearningCardCreator> options = getDeckList();
        cardList.setItems(options);

        cardList.setOnAction(event -> {
            LearningCardCreator selectedCardDeckInfo = cardList.getValue();
            if (selectedCardDeckInfo != null) {
                // Ensure contentDAO is initialized
                if (contentDAO == null) {
                    contentDAO = new ContentDAO();
                }
                com.example.cab302tailproject.model.Material material = contentDAO.getMaterialContent(selectedCardDeckInfo.getMaterialID(), "learningCard");
                if (material != null) {
                    deck = new LearningCardDeck(material.getContent());
                    cardContent.setText(deck.getCurrentCard());
                } else {
                    cardContent.setText("Error: Deck content not found for MaterialID: " + selectedCardDeckInfo.getMaterialID());
                    deck = new LearningCardDeck("");
                }
            }
        });
    }

    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-student.fxml", sidebarAiAssistanceButton, true);
    }

    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-student.fxml", sidebarReviewButton, false);
    }

    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-student.fxml", sidebarAnalysisButton, true);
    }


    @FXML
    private void onFlipClicked(ActionEvent actionEvent) {
        if (deck != null) {
            deck.flip();
            cardContent.setText(deck.getCurrentCard());
        }
    }

    @FXML private void onEasyClicked(ActionEvent actionEvent) {
        handleCardNavigation(true);
        if (deck != null) cardContent.setText(deck.easyNext());
    }

    @FXML private void onHardClicked(ActionEvent actionEvent) {
        handleCardNavigation(false);
        if (deck != null) cardContent.setText(deck.hardNext());
    }

    @FXML private void onMediumClicked(ActionEvent actionEvent) {
        handleCardNavigation(false);
        if (deck != null) cardContent.setText(deck.mediumNext());
    }

    @FXML private void onAgainClicked(ActionEvent actionEvent) {
        handleCardNavigation(false);
        if (deck != null) cardContent.setText(deck.hardNext());
    }

    @FXML private void onNextClicked(ActionEvent actionEvent) {
        handleCardNavigation(true);
        if (deck != null) cardContent.setText(deck.easyNext());
    }

    @FXML
    private void onCorrectClicked(ActionEvent event) {
        System.out.println("Correct button clicked");
        saveStudentResponse(true);
        if (deck != null) {
            cardContent.setText(deck.easyNext()); // Calls existing method
        }
    }

    @FXML
    private void onIncorrectClicked(ActionEvent event) {
        System.out.println("Incorrect button clicked");
        saveStudentResponse(false);
        if (deck != null) {
            cardContent.setText(deck.hardNext()); // Calls existing method
        }
    }

    private void handleCardNavigation(boolean isCorrect) {
        System.out.println("Navigating card, marked as: " + (isCorrect ? "Correct" : "Incorrect"));
    }


    private void saveStudentResponse(boolean isCorrect) {
        UserSession session = UserSession.getInstance();
        String studentEmail = session.getEmail();
        LearningCardCreator selectedDeckInfo = cardList.getValue();

        if (studentEmail == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student logged in.");
            return;
        }
        if (deck == null || deck.isEmpty() || LearningCardDeck.EMPTY_MESSAGE.equals(deck.getCurrentCard())) {
            showAlert(Alert.AlertType.WARNING, "No Card", "No card is currently displayed or deck is empty.");
            return;
        }
        if (selectedDeckInfo == null) {
            showAlert(Alert.AlertType.WARNING, "No Deck", "No deck selected.");
            return;
        }

        // Ensure DAOs are initialized
        if (studentDAO == null) {
            studentDAO = new SqlStudentDAO();
        }
        if (contentDAO == null) {
            contentDAO = new ContentDAO();
        }

        int studentID = studentDAO.getStudentIDByEmail(studentEmail);
        if (studentID == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not find student ID for email: " + studentEmail);
            return;
        }
        int materialID = selectedDeckInfo.getMaterialID();

        String cardQuestion = deck.getCurrentQuestionText();

        if (cardQuestion == null || cardQuestion.equals(deck.getCurrentCard()) && deck.isCurrentCardFlipped()) {
            if(deck.isCurrentCardFlipped()){
                deck.flip();
                cardQuestion = deck.getCurrentQuestionText();
                deck.flip();
            } else {
                cardQuestion = deck.getCurrentQuestionText();
            }
            if(cardQuestion == null || cardQuestion.isEmpty() || LearningCardDeck.EMPTY_MESSAGE.equals(cardQuestion)){
                showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve current card question.");
                return;
            }
        }

        int classroomID = studentDAO.getClassroomIDForStudent(studentID);
        if (classroomID == -1) {
            System.out.println("Warning: Student not associated with a classroom, or error fetching classroom. Saving response without classroom ID.");
            // classroomID will be handled as potentially NULL by addStudentCardResponse if it's <=0
        }

        boolean success = contentDAO.addStudentCardResponse(studentID, materialID, cardQuestion, isCorrect, classroomID);

        if (success) {
            System.out.println("Response saved: StudentID=" + studentID + ", MaterialID=" + materialID + ", Q=' " + (cardQuestion != null ? cardQuestion.substring(0, Math.min(cardQuestion.length(), 20)) : "N/A") +"...', Correct=" + isCorrect + ", ClassroomID=" + classroomID);
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save the response to the database.");
        }
    }

    @FXML private void logoutButtonClicked(ActionEvent actionEvent) throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", sidebarAnalysisButton, true);

    }
}
