package com.example.projetjavafx.root.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class EditProfileController {



        @FXML
        private ImageView profileImageView;

        @FXML
        private TextField txtName;

        @FXML
        private Button btnSaveChanges;

        @FXML
        private Button btnBack;

        private String profileImagePath;

        @FXML
        public void initialize() {
            // Initialize default profile details (optional)
            txtName.setText("Aya");

            // Handle save button
            btnSaveChanges.setOnAction(e -> saveProfileChanges());

            // Handle back button
            btnBack.setOnAction(e -> goBack());
        }

        // Method to change profile photo (now triggered by clicking the image)
        @FXML
        private void changeProfileImage() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());

            if (file != null) {
                profileImagePath = file.getAbsolutePath();
                profileImageView.setImage(new javafx.scene.image.Image("file:" + profileImagePath));
            }
        }

        // Method to save profile changes
        @FXML
        private void saveProfileChanges() {
            String name = txtName.getText();

            if (profileImagePath == null || profileImagePath.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Image Selected");
                alert.setHeaderText("Please select a profile image.");
                alert.showAndWait();
                return;
            }

            // Save the changes (e.g., save to a database or a file)
            System.out.println("Profile saved: " + name + ", " + profileImagePath);

            // Optionally, show a confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Updated");
            alert.setHeaderText("Your profile has been updated successfully.");
            alert.showAndWait();
        }

        // Go back to the previous page (Dashboard)
        private void goBack() {
            // Navigate to your Dashboard or the previous screen here
        }
    }
