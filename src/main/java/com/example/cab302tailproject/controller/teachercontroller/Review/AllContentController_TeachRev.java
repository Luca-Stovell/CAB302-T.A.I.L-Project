package com.example.cab302tailproject.controller.teachercontroller.Review;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.navigateToContent;

public class AllContentController_TeachRev {

    //<editor-fold desc="Field declarations">
    /**
     * A VBox container dynamically populated with content for managing and displaying
     * lesson plans or associated materials in the LessonPlanController.
     */
    @FXML
    private VBox dynamicContentBox;

    /**
     * Holds a reference to the previously displayed view within the application.
     * Used to facilitate navigation between views by tracking the last active view.
     * This is typically updated when transitioning between different scenes in the interface.
     */
    private VBox previousView;

    /**
     * The `contentDAO` variable is a private instance of the `IContentDAO` interface used to interact
     * with the content database.
     */
    private IContentDAO contentDAO;

    /**
     * Represents the material currently being edited or managed by the LessonPlanController.
     * This field holds a reference to the Material object containing relevant metadata
     */
    private Material currentMaterial;

    @FXML
    private TableView<ContentTableData> tableView;

    @FXML
    private TableColumn<ContentTableData, String> lastModifiedColumn;

    @FXML
    private TableColumn<ContentTableData, String> weekColumn;

    @FXML
    private TableColumn<ContentTableData, String> topicColumn;

    @FXML
    private TableColumn<ContentTableData, String> typeColumn;

    @FXML
    private TableColumn<ContentTableData, String> classroomColumn;
    //</editor-fold>

    //<editor-fold desc="Initialisation">
    /**
     * Initializes the controller by setting up the required data access objects and
     * binding the TableColumn properties to the corresponding fields of the data model.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        this.contentDAO = new ContentDAO();
        // Bind TableColumns to ContentTableData properties
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        classroomColumn.setCellValueFactory(new PropertyValueFactory<>("classroom"));

        reloadTableData();
    }

    /**
     * Initializes the data required for the current view by setting the reference to the previous view.
     *
     * @param previousView the VBox representing the previous view to be restored when navigating back
     */
    public void initData(VBox previousView) {
        this.previousView = previousView;
    }

    /**
     * Initialises or reloads the data displayed in the table by fetching updated content associated with the current teacher
     * based on the email stored in the user's session.
     */
    public void reloadTableData() {
        String teacherEmail = UserSession.getInstance().getEmail();
        ObservableList<ContentTableData> newData = contentDAO.fetchContentTableData(teacherEmail);
        tableView.setItems(newData);
    }
    //</editor-fold>

    //<editor-fold desc"Button handling">
    /**
     * Handles the event triggered when the "Back" button is clicked.
     * This restores the previous view if one is available by clearing the current dynamic content and replacing it with the
     * children of the previous view. Also deletes the content associated with the current material ID from the database.
     * If no previous view exists, it displays a warning alert to the user.
     */
    @FXML
    private void onBackClicked() throws IOException {
        if (previousView != null) {
            if (previousView.getChildren().size() == 1) {
                dynamicContentBox.getChildren().clear();
                dynamicContentBox.getChildren().addAll(previousView.getChildren());
                return;
            }
        }
        // Moving to new view
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher-overview.fxml"));
        VBox layout = fxmlLoader.load();

        dynamicContentBox.getChildren().clear();
        dynamicContentBox.getChildren().addAll(layout);
    }

    /**
     * Handles the event triggered when a user selects and clicks on a row in the table
     * to view the corresponding content. Checks if a row in the TableView is selected,
     * and retrieves its information to create a new Material instance with these values,
     * navigating to the content page associated with the selected material.
     */
    @FXML
    public void onViewClicked() {
        ContentTableData selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Item Selected",
                    "Please select an item from the table to view.");
            return;
        }
        String type = selectedItem.type();
        int materialID = selectedItem.materialID();

        currentMaterial = new Material(materialID, type);
        navigateToContentPage();
    }

    /**
     * Handles the event triggered when the delete button is clicked. Retrieves the currently selected item from the table view.
     * If a valid item is selected, the method retrieves the material ID of the selected item,
     * attempts to delete the associated content from the database using the data access object (contentDAO),
     * and reloads the table data to reflect the changes.
     */
    public void onDeleteClicked() {
        try {
            ContentTableData selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                showAlert(Alert.AlertType.WARNING, "No Item Selected",
                        "Please select an item from the table to view.");
                return;
            }
            int materialID = selectedItem.materialID();
            String selectedType = selectedItem.type();

            System.out.println("Deleting content with materialID: " + materialID);
            contentDAO.deleteContent(materialID, selectedType);
            reloadTableData();
        }
        catch (Exception e){
            showAlert(Alert.AlertType.WARNING, "Failure", "Could not delete selected material.");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">
    /**
     * Navigates to a new content page based on the currentMaterial.
     * Preserves the current view to facilitate return navigation and replaces
     * the content within the dynamic container with the new view.
     * Adds a property to previousView to allow reloading the table's content.
     */
    private void navigateToContentPage() {
        // Setup previous view by allowing the table to reload when returning here
        previousView = new VBox();
        previousView.getChildren().setAll(dynamicContentBox.getChildren());
        previousView.getProperties().put("controller", this);

        navigateToContent("review-teacher-lesson_view.fxml",
                dynamicContentBox,
                null,
                currentMaterial,
                (ContentViewController__TeachRev controller) ->
                        controller.initData(currentMaterial, dynamicContentBox, previousView
                        )
        );
    }
    //</editor-fold>

}
