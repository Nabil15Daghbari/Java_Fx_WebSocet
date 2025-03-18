package com.example.projetjavafx.root.organizer;

import com.example.projetjavafx.root.jobApplications.JobApplicationsController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrganizerController {

    // Event Table Components
    @FXML private TableView<Map<String, String>> eventsTable;
    @FXML private TableColumn<Map<String, String>, String> eventNameColumn;
    @FXML private TableColumn<Map<String, String>, String> eventDescColumn;
    @FXML private TableColumn<Map<String, String>, String> eventStartColumn;
    @FXML private TableColumn<Map<String, String>, String> eventEndColumn;
    @FXML private TableColumn<Map<String, String>, String> eventLocationColumn;

    // Job Table Components
    @FXML private TableView<Map<String, String>> jobsTable;
    @FXML private TableColumn<Map<String, String>, String> jobTitleColumn;
    @FXML private TableColumn<Map<String, String>, String> eventTitleColumn;
    @FXML private TableColumn<Map<String, String>, String> jobLocationColumn;
    @FXML private TableColumn<Map<String, String>, String> employmentTypeColumn;
    @FXML private TableColumn<Map<String, String>, String> applicationDeadlineColumn;
    @FXML private TableColumn<Map<String, String>, String> minSalaryColumn;
    @FXML private TableColumn<Map<String, String>, String> maxSalaryColumn;
    @FXML private TableColumn<Map<String, String>, String> currencyColumn;
    @FXML private TableColumn<Map<String, String>, String> jobDescColumn;
    @FXML private TableColumn<Map<String, String>, String> recruiterNameColumn;
    @FXML private TableColumn<Map<String, String>, String> recruiterEmailColumn;
    @FXML private TableColumn<Map<String, String>, String> createdAtColumn;
    private String fatalError;

    @FXML
    public void initialize() {
        setupEventTableColumns();
        setupJobTableColumns();
        loadData();
    }


    private void setupEventTableColumns() {
        eventNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("name")));
        eventDescColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("description")));
        eventStartColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("start_time")));
        eventEndColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("end_time")));
        eventLocationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("location")));
    }

    private void setupJobTableColumns() {
        jobTitleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("job_title")));
        eventTitleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("event_title")));
        jobLocationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("job_location")));
        employmentTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("employment_type")));
        applicationDeadlineColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("application_deadline")));
        minSalaryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("min_salary")));
        maxSalaryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("max_salary")));
        currencyColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("currency")));
        jobDescColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("job_description")));
        recruiterNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("recruiter_name")));
        recruiterEmailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("recruiter_email")));
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("created_at")));
    }

    private void loadData() {
        try {
            int currentUserId = 1; // Replace with actual session management

            // Load events
            List<Map<String, String>> events = OrganizerRepository.getOrganizerEvents(currentUserId);
            ObservableList<Map<String, String>> eventItems = FXCollections.observableArrayList(events);
            eventsTable.setItems(eventItems);

            // Load jobs
            List<Map<String, String>> jobs = OrganizerRepository.getOrganizerJobs(currentUserId);
            ObservableList<Map<String, String>> jobItems = FXCollections.observableArrayList(jobs);
            jobsTable.setItems(jobItems);

        } catch (SQLException e) {
            e.printStackTrace();
            // Show an error alert
        }




    }

    @FXML
    public void onNewJobPostClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/organizer/create-job-offer-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onCreateJobClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/organizer/create-job-offer-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleHomeButton(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/root/root-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }




    public void onAnalyticsClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/organizer/analytics-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void onJobFeedButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/jobfeed/job-feed-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void onReviewApplicationsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/projetjavafx/JobApplications/application_review-view.fxml"
            ));

            Parent root = loader.load();

            // Ensure the controller is correctly initialized
            JobApplicationsController controller = loader.getController();
            int yourActualJobId = 1; // Replace with actual job ID
            controller.setJobId(yourActualJobId);

            // Get the current scene and update the root
            Scene scene = ((Button) event.getSource()).getScene();
            scene.setRoot(root); // Set the new content without opening a new window

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Job Applications View.");
        }
    }


    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onAppliedAtButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/jobApplications/applied-jobs.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}

