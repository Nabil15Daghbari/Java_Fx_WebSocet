package com.example.projetjavafx.root.organizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AnalyticsController {

    @FXML private ComboBox<EventStats> eventComboBox;
    @FXML private Label totalParticipantsLabel;
    @FXML private Label maleParticipantsLabel;
    @FXML private Label femaleParticipantsLabel;
    @FXML private BarChart<String, Number> genderBarChart;
    @FXML private LineChart<String, Number> participationLineChart;

    private int organizerId;

    @FXML
    public void initialize() throws SQLException {
        setupChartAxes();
        setupComboBoxListener();
        System.out.println("AnalyticsController initialized.");
        loadEventData();

        // Do not call loadEventData() here because organizerId may not yet be set.
    }

    /**
     * Call this method from your main application or parent controller with a valid organizer ID.
     */
    public void setOrganizerId(int organizerId) throws SQLException {
        this.organizerId = organizerId;
        System.out.println("Organizer ID set to: " + organizerId);
        loadEventData();

    }

    private void setupChartAxes() {
        // Configure BarChart axes
        CategoryAxis barXAxis = (CategoryAxis) genderBarChart.getXAxis();
        NumberAxis barYAxis = (NumberAxis) genderBarChart.getYAxis();
        barXAxis.setLabel("Gender");
        barYAxis.setLabel("Participants");

        // Configure LineChart axes
        CategoryAxis lineXAxis = (CategoryAxis) participationLineChart.getXAxis();
        NumberAxis lineYAxis = (NumberAxis) participationLineChart.getYAxis();
        lineXAxis.setLabel("Events");
        lineYAxis.setLabel("Total Participants");
    }

    private void loadEventData() {
        System.out.println("Loading event data for organizer ID: " + organizerId);
        try {
            EventStats stats = AnalyticsRepository.getEventStats(organizerId);
            List<EventAgeStats> ageStats = AnalyticsRepository.getEventAvgAgeStats();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int i = 0 ;i <ageStats.size() ; i++){
                series.getData().add(new XYChart.Data<>(ageStats.get(i).getEventName(), ageStats.get(i).getAverageAge()));
            }
            participationLineChart.getData().add(series);

            eventComboBox.getItems().clear();
            eventComboBox.getItems().addAll(stats);
            updateGenderChart(stats);



        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load event data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupComboBoxListener() {
        eventComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Selected event: " + newVal.getEventName());
                updateStatsDisplay(newVal);
                updateGenderChart(newVal);
            }
        });
    }

    private void updateStatsDisplay(EventStats stats) {
        totalParticipantsLabel.setText(String.valueOf(stats.getTotalParticipants()));
        maleParticipantsLabel.setText(String.valueOf(stats.getMaleCount()));
        femaleParticipantsLabel.setText(String.valueOf(stats.getFemaleCount()));
    }

    private void updateGenderChart(EventStats stats) {
        genderBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Male", stats.getMaleCount()));
        series.getData().add(new XYChart.Data<>("Female", stats.getFemaleCount()));
        genderBarChart.getData().add(series);
        System.out.println("Updated gender chart: Male=" + stats.getMaleCount() + ", Female=" + stats.getFemaleCount());
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadView(String fxmlPath, javafx.event.ActionEvent event) {
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
    protected void onDashboardClick(javafx.event.ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/organizer-view.fxml", event);
    }

    @FXML
    protected void onEventsClick(javafx.event.ActionEvent event) {
        loadView("/com/example/projetjavafx/events/events-view.fxml", event);
    }

    @FXML
    protected void onAnalyticsClick(javafx.event.ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/analytics-view.fxml", event);
    }

    public void onJobApplicationsButtonClick(javafx.event.ActionEvent event) {
        loadView("/com/example/projetjavafx/JobApplications/application_review-view.fxml", event);
    }

    public void onJobFeedButtonClick(javafx.event.ActionEvent event) {
        loadView("/com/example/projetjavafx/jobfeed/job-feed-view.fxml", event);
    }

    public void onCreateJobButtonClick(javafx.event.ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/create-job-offer-view.fxml", event);
    }

    public void onHomeButtonClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/root/root-view.fxml", event);
    }


}
