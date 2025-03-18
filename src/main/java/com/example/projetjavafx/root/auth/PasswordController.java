package com.example.projetjavafx.root.auth;

import com.example.projetjavafx.root.auth.PasswordRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class PasswordController {

    @FXML
    private TextField newpassword;

    @FXML
    private TextField comfirmpassword;

    @FXML
    private Button buttonconfirmpass;

    private String email; // Récupéré depuis UsernameController

    private PasswordRepository passwordModel = new PasswordRepository();

    public void setEmail(String email) {
        this.email = email;
        System.out.println("Email reçu dans PasswordController: " + email); // Debug
    }

    @FXML
    private void initialize() {
        buttonconfirmpass.setOnAction(event -> changePassword());
    }

    private void changePassword() {
        String newPass = newpassword.getText();
        String confirmPass = comfirmpassword.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Les champs ne doivent pas être vides.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        if (passwordModel.updatePassword(email, newPass)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe modifié avec succès !");
            openLoginView();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé !");
        }
    }

    private void openLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/auth/login-view.fxml"));
            Stage stage = (Stage) buttonconfirmpass.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de connexion.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}