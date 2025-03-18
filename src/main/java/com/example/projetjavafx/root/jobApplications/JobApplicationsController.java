package com.example.projetjavafx.root.jobApplications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;



public class JobApplicationsController {

    public Button dashboardButton;
    public Button homeButton;
    public Button JobFeedButton;
    public Button jobFeedButton;
    public Button createJobButton;
    public Button eventsButton;
    public Button analyticsButton;
    @FXML private TableView<Application> applicationsTable;
    @FXML private TableColumn<Application, Number> applicationIdColumn;
    @FXML private TableColumn<Application, Number> userIdColumn;
    @FXML private TableColumn<Application, String> coverLetterColumn;
    @FXML private TableColumn<Application, String> resumeColumn;
    @FXML private TableColumn<Application, String> statusColumn;
    @FXML private TableColumn<Application, Void> actionColumn;

    private final ObservableList<Application> applications = FXCollections.observableArrayList();
    private int userId;

    @FXML
    public void initialize() {
        setupTableColumns();
        // For demonstration, we set a default user id.
        // Replace this with your actual user context.
        setUserId(1);
    }

    private void setupTableColumns() {
        // Bind columns to Application properties
        applicationIdColumn.setCellValueFactory(cellData -> cellData.getValue().applicationIdProperty());
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        coverLetterColumn.setCellValueFactory(cellData -> cellData.getValue().coverLetterProperty());
        resumeColumn.setCellValueFactory(cellData -> cellData.getValue().resumePathProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Custom cell for Cover Letter with wrapping text
        coverLetterColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setWrapText(true);
            }
        });

        // Hyperlink cell for Resume
        resumeColumn.setCellFactory(column -> new TableCell<>() {
            private final Hyperlink hyperlink = new Hyperlink("View Resume");
            {
                hyperlink.setOnAction(event -> handleViewResume(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hyperlink);
            }
        });

        // Editable status column with ComboBox
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList("pending", "accepted", "rejected")));
        statusColumn.setOnEditCommit(event -> {
            Application application = event.getRowValue();
            application.setStatus(event.getNewValue());
//            handleStatusUpdate(application); // Automatically update the status in the database
        });

        // Action column with an Update button
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            {
                updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                updateButton.setOnAction(event -> handleStatusUpdate(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : updateButton);
            }
        });

        applicationsTable.setItems(applications);
        applicationsTable.setEditable(true);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID set: " + userId);
        loadApplicationsForUserJobs();
    }

    private void loadApplicationsForUserJobs() {
        try {
            List<Application> applicationList = JobApplicationsRepository.getApplicationsForUserPostedJobs(userId);
            System.out.println("Applications fetched: " + applicationList.size());
            applications.setAll(applicationList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load applications", e.getMessage());
        }
    }

    private void handleStatusUpdate(Application application) {
        try {
            Optional<ButtonType> result = showConfirmationDialog(
                    "Update Status",
                    "Are you sure you want to update the application status to '" + application.getStatus() + "'?"
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                JobApplicationsRepository.updateApplicationStatus(application.getApplicationId(), application.getStatus());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Status Updated", "Application status updated successfully");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update status", e.getMessage());
        }
    }

    private void handleViewResume(Application application) {
        // Check if the resumePath is null or empty
        if (application.getResumePath() == null || application.getResumePath().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Resume Not Found", "The resume file path is not specified.");
            return;
        }

        // Create a File object from the resumePath
        File resumeFile = new File(application.getResumePath());

        // Check if the file exists
        if (!resumeFile.exists()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Resume Not Found", "The resume file could not be located at: " + application.getResumePath());
            return;
        }

        // Check if the file is a PDF
        if (!resumeFile.getName().toLowerCase().endsWith(".pdf")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid File Type", "The resume file must be a PDF.");
            return;
        }

        // Try to open the file using the default PDF viewer
        try {
            // Wrap the file path in double quotes to handle spaces and special characters
            String quotedFilePath = "\"" + resumeFile.getAbsolutePath() + "\"";
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", quotedFilePath});
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could Not Open Resume", "An error occurred while trying to open the resume: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public void setJobId(int yourActualJobId) {
        // Implement this method if needed
    }

    private void loadView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the current stage (instead of opening a new one)
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Set the new root to the same scene
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onDashboardClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/organizer-view.fxml", event);
    }

    @FXML
    protected void onEventsClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/events/events-view.fxml", event);
    }

    @FXML
    protected void onAnalyticsClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/analytics-view.fxml", event);
    }

    public void onJobApplicationsButtonClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/JobApplications/application_review-view.fxml", event);
    }

    public void onJobFeedButtonClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/jobfeed/job-feed-view.fxml", event);
    }

    public void onCreateJobButtonClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/create-job-offer-view.fxml", event);
    }

    public void onHomeButtonClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/root/root-view.fxml", event);
    }
}