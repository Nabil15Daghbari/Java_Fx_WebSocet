package com.example.projetjavafx.root.events;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.auth.SessionManager;
import com.mysql.cj.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class CreateEventController {


    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField locationField;

    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ImageView imagePreview;


    private String base64Image; // Stores the encoded image

    @FXML
    public void initialize() {
//        loadOrganizers();
        loadCategories();
    }

    @FXML
    public void onUploadImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                base64Image = Base64.getEncoder().encodeToString(fileContent);

                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    @FXML
    public void onSaveEventClick() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String location = locationField.getText();
        String category = categoryComboBox.getValue();

        if (name.isEmpty() || startDate == null || endDate == null
                || category == null || base64Image == null) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        // Check that start date is strictly before the end date
        if (!startDate.isBefore(endDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Date");
            alert.setHeaderText(null);
            alert.setContentText("The start date must be before the end date.");
            alert.showAndWait();
            return;
        }

        int temporaryOrganizerId = SessionManager.getInstance().getCurrentUserId();
        int categoryId = Integer.parseInt(category.split(" - ")[0]);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Connection connection = AivenMySQLManager.getConnection()) {
            String query = "INSERT INTO Events (name, description, start_time, end_time, location, organizer_id, category_id, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, startDate.format(formatter));
            statement.setString(4, endDate.format(formatter));
            statement.setString(5, location);
            statement.setInt(6, temporaryOrganizerId);
            statement.setInt(7, categoryId);
            statement.setString(8, base64Image);

            statement.executeUpdate();
            System.out.println("Événement ajouté avec succès !");
            goToEventsPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





//    private void loadOrganizers() {
//        ObservableList<String> organizers = FXCollections.observableArrayList();
//        try (Connection connection = AivenMySQLManager.getConnection()) {
//            String query = "SELECT user_id, username FROM Users";
//            ResultSet resultSet = connection.createStatement().executeQuery(query);
//
//            while (resultSet.next()) {
//                String organizer = resultSet.getInt("user_id") + " - " + resultSet.getString("username");
//                organizers.add(organizer);
//            }
//            organizerComboBox.setItems(organizers);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void loadCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        try (Connection connection = AivenMySQLManager.getConnection()) {
            String query = "SELECT category_id, name FROM Categories";
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                String category = resultSet.getInt("category_id") + " - " + resultSet.getString("name");
                categories.add(category);
            }
            categoryComboBox.setItems(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void onCancelClick() {

        goToEventsPage();

    }




    private void goToEventsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/events/events-view.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/root/root-view.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAnalyticsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/organizer/analytics-view.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
