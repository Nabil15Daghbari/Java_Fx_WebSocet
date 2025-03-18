package com.example.projetjavafx.root.social;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class PostController {

    // Références FXML
    @FXML private TextArea postContent;
    @FXML private Button publishButton;
    @FXML private ImageView mediaPreview;
    @FXML private StackPane mediaPreviewContainer;
    @FXML private ComboBox<String> privacyComboBox;

    private SocialController socialController;
    private File selectedFile;

    public void setSocialController(SocialController controller) {
        this.socialController = controller;
    }

    @FXML
    public void initialize() {
        configurePrivacyOptions();
        setupDragAndDrop();
        publishButton.setDisable(true);

        postContent.textProperty().addListener((obs, oldVal, newVal) ->
                checkPublishButtonState()
        );
    }

    private void configurePrivacyOptions() {
        privacyComboBox.getItems().addAll("Moi uniquement", "Amis", "Public");
        privacyComboBox.setValue("Moi uniquement");
    }

    private void setupDragAndDrop() {
        mediaPreviewContainer.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        mediaPreviewContainer.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty()) {
                handleFileSelection(files.get(0));
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    @FXML
    private void handleAddMediaClick(MouseEvent event) {
        chooseFile();
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.mov")
        );

        File file = fileChooser.showOpenDialog(publishButton.getScene().getWindow());
        if (file != null) {
            handleFileSelection(file);
        }
    }

    private void handleFileSelection(File file) {
        selectedFile = file;
        if (file.getName().matches("(?i).*\\.(mp4|mov)$")) {
            mediaPreview.setVisible(false);
        } else {
            mediaPreview.setImage(new Image(file.toURI().toString()));
            mediaPreview.setVisible(true);
        }
        mediaPreviewContainer.setVisible(true);
        checkPublishButtonState();
    }

    @FXML
    private void removeMedia() {
        selectedFile = null;
        mediaPreview.setImage(null);
        mediaPreviewContainer.setVisible(false);
        checkPublishButtonState();
    }

    private void checkPublishButtonState() {
        boolean hasContent = !postContent.getText().trim().isEmpty();
        boolean hasMedia = selectedFile != null;
        publishButton.setDisable(!(hasContent || hasMedia));
    }

    @FXML
    private void handlePostSubmission() {
        String content = postContent.getText().trim();
        String imagePath = null;

        if (selectedFile != null) {
            String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            // Use a dedicated folder (e.g., "images/") instead of the resources folder
            String targetDir = "images/";
            File targetFile = new File(targetDir + uniqueFileName);

            try {
                new File(targetDir).mkdirs(); // Ensure directory exists
                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Store the absolute path for later retrieval
                imagePath = targetFile.getAbsolutePath();
            } catch (IOException e) {
                showAlert("Error", "Failed to save image: " + e.getMessage());
                return;
            }
        }


        String privacy = privacyComboBox.getValue();

        Post newPost = new Post(
                1, // Replace with actual user ID
                content,
                null, // eventId
                imagePath
        );

        if (socialController != null) {
            socialController.addPostToFeed(newPost);
        }

        closeWindow();
    }

    private Account getCurrentUser() {
        // Remplacer par la logique réelle d'authentification
        return new Account("Hadil Lajili", "/com/example/projetjavafx/social/img/userprofile.png", true);
    }

    private void closeWindow() {
        ((Stage) publishButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}