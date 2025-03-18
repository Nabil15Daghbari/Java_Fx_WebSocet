package com.example.projetjavafx.root.events;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class EventDetailsController {

    @FXML
    private ImageView eventImage;
    @FXML
    private Label eventName;
    @FXML
    private Text eventDescription;
    @FXML
    private Text eventDate;
    @FXML
    private Text eventLocation;


    @FXML
    public void initialize() {
        // Appliquer le style CSS global à la fenêtre popup
        eventImage.getStyleClass().add("popup-image");
        eventName.getStyleClass().add("popup-title");
        eventDescription.getStyleClass().add("popup-text");
        eventDate.getStyleClass().add("popup-text");
        eventLocation.getStyleClass().add("popup-text");
    }


    public void setEventDetails(String name, String description, String startDate, String endDate, String location, String imageBase64) {
        eventName.setText(name);
        eventDescription.setText(description);
        eventDate.setText("Date: " + startDate + " - " + endDate);
        eventLocation.setText("Location: " + location);

        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            eventImage.setImage(image);
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) eventName.getScene().getWindow();
        stage.close();
    }
}
