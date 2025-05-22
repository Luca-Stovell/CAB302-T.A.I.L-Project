package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.LearningCards.LearningCardDeck;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.LearningCardCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LearningCardController {

    @FXML private ComboBox<LearningCardCreator> cardList;
    /**
     * Button in the sidebar, potentially for navigating to a content generation or task area relevant to students.
     */
    @FXML
    private Button sidebarGenerateButton;
    /**
     * Button in the sidebar, potentially for students to review their work or feedback.
     */
    @FXML private Button sidebarReviewButton;
    /**
     * Button in the sidebar, possibly for students to view performance analysis or progress.
     */
    @FXML private Button sidebarAnalysisButton;
    /**
     * Button in the sidebar to navigate to or refresh the AI assistance view (this current view).
     */
    @FXML private Button sidebarAiAssistanceButton;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Top Navigation">
    /**
     * Button in the top navigation bar to navigate to a home screen or student dashboard.
     */
    @FXML private Button homeButton;
    /**
     * Button in the top navigation bar to access student-specific settings or preferences.
     */
    @FXML private Button settingsButton;

    /**
     * Button that changes the side output of the current flashcard
     */
    @FXML private Button flipButton;
    /**
     * Button that sends the current flashcard to the end of the deck
     */
    @FXML private Button easyButton;
    /**
     * Button that sends the current flashcard to the start of the deck
     */
    @FXML private Button hardButton;
    /**
     * Button that sends the current flashcard to the start of the deck
     */
    @FXML private Button againButton;
    /**
     * Button that sends the current flashcard to the middle of the deck
     */
    @FXML private Button mediumButton;
    /**
     * Button that sends the current flashcard to the end of the deck
     */
    @FXML private Button nextButton;
    /**
     * Field displaying the contents of the flashcards
     */
    @FXML private TextArea cardContent;

    // this is part of a common UI element
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Student Top Nav: Home button clicked - Navigation or action needed.");
        // TODO: Implement navigation to the student home/dashboard view
    }

    /**
     * The current deck
     */
    private LearningCardDeck deck;

    private ContentDAO contentDAO;

    /**
     * Retrieves a deck from the database by id, and stored it in deck
     * @param ID Database's materialID of the selected deck
     */
    private void getDeck(int ID){
        //String mockDeck = "1. What is Java?::A high-level, object-oriented programming language developed by Sun Microsystems,, 2. What does JVM stand for?::Java Virtual Machine,, 3. What is the purpose of the JVM?::To execute Java bytecode on any platform, enabling platform independence,, 4. What is the difference between JDK and JRE?::JDK includes tools for developing Java programs, while JRE is for running them,, 5. What is bytecode in Java?::An intermediate code generated after compiling Java code, which runs on the JVM,, 6. What keyword is used to create a class in Java?::class,, 7. What keyword is used to inherit a class in Java?::extends,, 8. What is the default value of an int in Java?::0,, 9. What does the 'static' keyword mean in Java?::It means the method or variable belongs to the class rather than instances of it,, 10. What is method overloading?::Defining multiple methods with the same name but different parameters,, 11. What is method overriding?::Redefining a method in a subclass that is already defined in the superclass,, 12. What is the 'main' method signature in Java?::public static void main(String[] args),, 13. What is the purpose of the 'final' keyword?::To declare constants, prevent method overriding or inheritance,, 14. What is the difference between == and .equals() in Java?::'==' compares references, '.equals()' compares values (usually),, 15. What is an interface in Java?::An abstract type used to specify a set of methods that a class must implement";
        String dbDeck = contentDAO.getLearningCardContent(ID);
        deck = new LearningCardDeck(dbDeck);
        cardContent.setText(deck.getCurrentCard());
    }

    /**
     * Retrieves, from the database, a list of all learning card decks
     * @return a list of topics and ID of the decks
     */
    private ObservableList<LearningCardCreator> getDeckList(){
        // grab from database, the topics of all the learning cards
        // note: topics aren't unique
        return contentDAO.getAllCards();
    }

    /**
     * Initialises learning card page by loading in the deck and setting the initial display.
     * Also finds the list of learning cards, populates the comboBox with it, and defines onAction handling for the comboBox
     */
    @FXML public void initialize(){
        contentDAO = new ContentDAO();
        // sends welcome text to the display before a deck is selected
        deck = new LearningCardDeck(new ArrayList<>()
            {{
                add(new String[]{"Select a lesson card deck to begin", "(Use the dropdown menu on the right)"});
            }}
        );
        cardContent.setText(deck.getCurrentCard());

        // populate comboBox
        ObservableList<LearningCardCreator> options = getDeckList();
        cardList.setItems(options);

        // sets cardList to call an anonymous function that runs getDeck
        cardList.setOnAction(event -> {
            getDeck(cardList.getValue().getMaterialID());
        });
    }

    /**
     * Handler for the sidebar button that takes the user to the AI assistance page
     * @param event The {@link ActionEvent} triggered by the button click.
     */
    @FXML
    private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarGenerateButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("ai_assistant-student.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles action events for the "Review" button in the sidebar.
     * Placeholder for navigation or review functionality.
     * @param event The {@link ActionEvent} triggered by the button click.
     */
    @FXML
    private void onSidebarReviewClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarReviewButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-student.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles action events for the "Analysis" button in the sidebar.
     * Placeholder for navigation or analysis functionality.
     * @param event The {@link ActionEvent} triggered by the button click.
     */
    @FXML
    private void onSidebarAnalysisClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarAnalysisButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("analytics-student.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }



    /**
     * Changes a cards face display and sends it to the main text area
     * @param actionEvent Event triggered by button click
     */
    @FXML
    private void onFlipClicked(ActionEvent actionEvent) {
        deck.flip();
        cardContent.setText(deck.getCurrentCard());
    }

    /**
     * Sends the current card to the back of the deck, and outputs the next card
     * @param actionEvent The {@link ActionEvent} triggered by the button click.
     */
    @FXML private void onEasyClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.easyNext());
    }

    /**
     * Sends the current card to near the front of the deck, and outputs the next card
     * @param actionEvent The {@link ActionEvent} triggered by the button click.
     */
    @FXML private void onHardClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.hardNext());
    }

    /**
     * Sends the current card to the middle of the deck, and outputs the next card
     * @param actionEvent The {@link ActionEvent} triggered by the button click.
     */
    @FXML private void onMediumClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.mediumNext());
    }

    /**
     * Sends the current card to near the front of the deck, and outputs the next card
     * @param actionEvent The {@link ActionEvent} triggered by the button click.
     */
    // not entirely sure what the again button is supposed to do. For now, it's just the same as hard
    @FXML private void onAgainClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.hardNext());
    }

    /**
     * Sends the current card to the back of the deck, and outputs the next card
     * @param actionEvent The {@link ActionEvent} triggered by the button click.
     */
    //again, not sure what this should do.
    @FXML private void onNextClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.easyNext());
    }
}
