package com.example.projetjavafx.root.group;

import com.example.projetjavafx.root.group.GroupAddRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class GroupAddController {
    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField rulesField;




    @FXML
    private ImageView imagePreview;

    private String imageBase64; // Pour stocker l'image encodée en Base64

    private GroupAddRepository groupModel = new GroupAddRepository();

    @FXML
    private void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void onUploadImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Convertir l'image en Base64
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

                // Afficher l'image dans l'ImageView
                Image image = new Image(new FileInputStream(selectedFile));
                imagePreview.setImage(image);

                fileInputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image.");
            }
        }
    }

    @FXML
    private void onCancelClick() {
        // Retourner à la page précédente
        loadView("/com/example/projetjavafx/group/group-add-view.fxml");
    }

    @FXML
    private void onSaveGroupClick() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String rules = rulesField.getText();

        if (name.isEmpty() || description.isEmpty() || rules.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (imageBase64 == null || imageBase64.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez télécharger une image.");
            return;
        }

        // Confirmation avant de sauvegarder
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment créer ce groupe ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (groupModel.saveGroup(name, description, rules, imageBase64)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Groupe créé avec succès !");
                loadView("/com/example/projetjavafx/group/group-profile-view.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer le groupe.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== Navbar Button Handlers =====================
    @FXML
    private void handleHomeButton() {
        loadView("/com/example/projetjavafx/root/home/home-view.fxml");
    }

    @FXML
    private void onAnalyticsClick() {
        loadView("/com/example/projetjavafx/root/analytics/analytics-view.fxml");
    }

    @FXML
    private void onCreateJobClick() {
        loadView("/com/example/projetjavafx/root/jobs/create-job-view.fxml");
    }

    @FXML
    private void onJobFeedButtonClick() {
        loadView("/com/example/projetjavafx/root/jobs/job-feed-view.fxml");
    }


}
