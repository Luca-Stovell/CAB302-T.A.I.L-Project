package com.example.cab302tailproject.controller.studentcontroller.Review;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;


public class MainFrameController_StudRev {
    public Label LoggedInName;
    @FXML private Button sidebarCardsButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAiAssistanceButton;

    //<editor-fold desc="Sidebar Buttons">
    @FXML
    public void onSidebarCardsClicked() throws IOException {
        loadScene("learning_cards.fxml", sidebarCardsButton, true);
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
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-student.fxml", sidebarAiAssistanceButton, true);
    }
    //</editor-field>

    //<editor-field desc="Utility methods">

    //</editor-field>
}
