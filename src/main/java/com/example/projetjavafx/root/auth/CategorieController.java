package com.example.projetjavafx.root.auth;

import com.example.projetjavafx.root.auth.CategorieRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
public class CategorieController {

    @FXML
    private CheckBox music, sport, gaming, education, culturel, social, technologique;

    @FXML
    private Button valide;

    private int userId;  // ID de l'utilisateur qui vient de s'inscrire

    private CategorieRepository categorieModel = new CategorieRepository();

    public void setUserId(int userId) {
        this.userId = userId;  // Sauvegarde l'ID de l'utilisateur
    }

    @FXML
    public void onValideClick() {
        try {
            if (music.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Music");
                categorieModel.insertUserInterest(userId, categoryId);
            }
            if (sport.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Sport");
                categorieModel.insertUserInterest(userId, categoryId);
            }
            if (gaming.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Gaming");
                categorieModel.insertUserInterest(userId, categoryId);
            }
            if (education.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Éducation");
                categorieModel.insertUserInterest(userId, categoryId);
            }
            if (culturel.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Culturel");
                categorieModel.insertUserInterest(userId, categoryId);
            }
            if (social.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Social");
                categorieModel.insertUserInterest(userId, categoryId);
            }
            if (technologique.isSelected()) {
                int categoryId = categorieModel.getCategoryId("Technologique");
                categorieModel.insertUserInterest(userId, categoryId);
            }

            System.out.println("Catégories et intérêts utilisateur ajoutés avec succès !");
            openLoginView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/auth/login-view.fxml"));
            Stage stage = (Stage) valide.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page de connexion.");
        }
    }

}
