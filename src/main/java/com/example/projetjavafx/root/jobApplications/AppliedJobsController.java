package com.example.projetjavafx.root.jobApplications;

import com.example.projetjavafx.root.organizer.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AppliedJobsController {

    @FXML private TableView<Job> jobsTable;
    @FXML private TableColumn<Job, Number> jobIdColumn;
    @FXML private TableColumn<Job, String> jobTitleColumn;
    @FXML private TableColumn<Job, String> eventTitleColumn;
    @FXML private TableColumn<Job, String> jobLocationColumn;
    @FXML private TableColumn<Job, String> applicationDeadlineColumn;
    @FXML private TableColumn<Job, String> recruiterColumn;
    @FXML private TableColumn<Job, Void> actionColumn;

    private final ObservableList<Job> jobs = FXCollections.observableArrayList();
    private int userId;

    @FXML
    public void initialize() {
        setupTableColumns();
        // For demonstration purposes, set a default user ID.
        // Replace this with the actual logged-in user’s ID.
        setUserId(1);
    }

    private void setupTableColumns() {
        jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        eventTitleColumn.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        jobLocationColumn.setCellValueFactory(new PropertyValueFactory<>("jobLocation"));
        applicationDeadlineColumn.setCellValueFactory(new PropertyValueFactory<>("applicationDeadline"));
        recruiterColumn.setCellValueFactory(new PropertyValueFactory<>("recruiterName"));

        // Action column: add a "View" button to each row for further job details.
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    handleViewJobDetails(job);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });

        jobsTable.setItems(jobs);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadJobsAppliedByUser();
    }

    private void loadJobsAppliedByUser() {
        try {
            List<Job> jobsList = com.example.projetjavafx.root.jobApplications.AppliedJobsRepository.getJobsAppliedByUser(userId);
            jobs.setAll(jobsList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load applied jobs", e.getMessage());
        }
    }

    private void handleViewJobDetails(Job job) {
        try {
            // Retrieve the application status for the given job and user.
            String status = AppliedJobsRepository.getApplicationStatusForJob(userId, job.getJobId());
            String message;
            if ("accepted".equalsIgnoreCase(status)) {
                message = "Congrats, you have been accepted!";
            } else if ("rejected".equalsIgnoreCase(status)) {
                message = "Sorry, you have been rejected. Better luck next time!";
            } else if ("pending".equalsIgnoreCase(status)) {
                message = "Your job application hasn't been reviewed yet. Please be patient.";
            } else {
                message = "Application status: " + status;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Job Application Status");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve application status", e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadView(String fxmlPath, ActionEvent event) {
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
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
