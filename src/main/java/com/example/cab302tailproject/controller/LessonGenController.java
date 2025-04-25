package com.example.cab302tailproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class LessonGenController {

    @FXML
    private ListView<String> dashboardListView;

    @FXML
    private Button filesButton;
    @FXML
    private Button studentsButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button settingsButton;

    @FXML
    private ToggleGroup generateToggleGroup;
    @FXML
    private RadioButton worksheetRadioButton;
    @FXML
    private RadioButton lessonPlanRadioButton;
    @FXML
    private TextField generatorTextField;
    @FXML
    private Button generateButton;

    @FXML
    public void initialize() {
        // Initialise toggling for radio buttons
        generateToggleGroup = new ToggleGroup();
        worksheetRadioButton.setToggleGroup(generateToggleGroup);
        lessonPlanRadioButton.setToggleGroup(generateToggleGroup);

        // Default radio button
        worksheetRadioButton.setSelected(true);
    }

    @FXML
    private void onGenerateClicked(MouseEvent mouseEvent) {
        String selectedGenerator = ((RadioButton) generateToggleGroup.getSelectedToggle()).getText();
        String userInput = generatorTextField.getText();

        if (userInput == null || userInput.isEmpty()) {
            showAlert("Error", "Please enter something to generate.");
        }

        // Handle the input
        System.out.println("Generating " + selectedGenerator + " with the input of: " + userInput);
    }

    // For alerts, can be used wherever?
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onHomeClicked(ActionEvent actionEvent) {
        dashboardListView.getSelectionModel().selectFirst();
    }



}
