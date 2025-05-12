package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.LearningCards.LearningCardDeck;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class LearningCardController {

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

    @FXML private Button flipButton;
    @FXML private Button easyButton;
    @FXML private Button hardButton;
    @FXML private Button againButton;
    @FXML private Button mediumButton;
    @FXML private Button nextButton;
    @FXML private TextArea cardContent;

    // this is part of a common UI element
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Student Top Nav: Home button clicked - Navigation or action needed.");
        // TODO: Implement navigation to the student home/dashboard view
    }

    LearningCardDeck deck;

    private void getDeck(int ID){
        //TODO implement getting from database
        //also, when deck lengths are small (like this one)
        String mockDeck = "question:answer,question2:answer2,question3:answer3,question4:answer4,question5:answer5";
        deck = new LearningCardDeck(mockDeck);
    }

    @FXML public void initialize(){
        // replace this with deck id when implemented
        getDeck(-1);
        cardContent.setText(deck.getCurrentCard());
    }

    @FXML
    private void onFlipClicked(ActionEvent actionEvent) {
        deck.flip();
        cardContent.setText(deck.getCurrentCard());
    }


    @FXML private void onEasyClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.easyNext());
    }

    @FXML private void onHardClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.hardNext());
        System.out.println("hard");
    }

    @FXML private void onMediumClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.mediumNext());
    }

    // not entirely sure what the again button is supposed to do. For now, it's just the same as hard
    @FXML private void onAgainClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.hardNext());
    }

    //again, not sure what this should do.
    @FXML private void onNextClicked(ActionEvent actionEvent) {
        cardContent.setText(deck.easyNext());
    }
}
