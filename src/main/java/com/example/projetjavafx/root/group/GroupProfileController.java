package com.example.projetjavafx.root.group;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.auth.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import javafx.scene.control.Alert;


public class GroupProfileController {
    @FXML
    private Button joinButton;
    @FXML
    private TextField searchField;

    @FXML
    private FlowPane groupsContainer;

    private GroupProfileRepository groupModel = new GroupProfileRepository();
    private GroupAddMemberRepository groupMemberModel = new GroupAddMemberRepository();

    @FXML
    public void initialize() {
        loadGroups("");
        // Listener pour filtrer les groupes lors de la saisie
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadGroups(newValue);
        });
    }

    private void loadGroups(String searchText) {
        groupsContainer.getChildren().clear(); // Efface les groupes existants

        try (ResultSet resultSet = groupModel.getGroups(searchText)) {
            if (resultSet == null) {
                showAlert("Erreur", "Problème de connexion à la base de données.");
                return;
            }

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String rules = resultSet.getString("rules");
                String imageBase64 = resultSet.getString("profile_picture");

                // Créer une carte de groupe
                VBox groupBox = new VBox(10);
                groupBox.getStyleClass().add("group-card");

                Text groupText = new Text(name + "\n" + description + "\n" + rules);
                groupText.getStyleClass().add("group-description");

                ImageView imageView = new ImageView();
                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    imageView.setImage(image);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                }
                imageView.getStyleClass().add("group-image");

                // Bouton "View Details"
                Button viewDetailsButton = new Button("View Details");
                viewDetailsButton.getStyleClass().add("group-button");
                viewDetailsButton.setOnAction(event -> showGroupDetails(name, description, rules, imageBase64));

                // Bouton "Add Member"
                Button addMemberButton = new Button("Add Member");
                addMemberButton.getStyleClass().add("group-button-secondary");
                addMemberButton.setOnAction(event -> showAddMemberDialog(name));

                // Bouton "Rejoindre"
                Button joinButton = new Button("Rejoindre");
                joinButton.getStyleClass().add("group-button-secondary");
                joinButton.setOnAction(event -> joinGroup(name));

                // Bouton "Afficher les membres"
                Button showMembersButton = new Button("Afficher les membres");
                showMembersButton.getStyleClass().add("group-button-secondary");
                showMembersButton.setOnAction(event -> showGroupMembers(name));

                // Conteneur des boutons
                VBox buttonContainer = new VBox(10, viewDetailsButton, addMemberButton, showMembersButton, joinButton);
                buttonContainer.getStyleClass().add("group-buttons");

                // Ajout des éléments à la carte
                groupBox.getChildren().addAll(imageView, groupText, buttonContainer);
                groupsContainer.getChildren().add(groupBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGroupDetails(String name, String description, String rules, String imageBase64) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/group/group-details.fxml"));
            Parent root = loader.load();

            GroupDetailsController controller = loader.getController();
            controller.setGroupDetails(name, description, rules, imageBase64);

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Ajouter le fichier CSS si nécessaire
            String cssFile = getClass().getResource("/com/example/projetjavafx/group/css/group_profile.css") != null ?
                    getClass().getResource("/com/example/projetjavafx/group/css/group_profile.css").toExternalForm() : null;

            if (cssFile != null) {
                scene.getStylesheets().add(cssFile);
            } else {
                System.out.println("⚠ Fichier CSS non trouvé !");
            }

            stage.setScene(scene);
            stage.setTitle("Group Details");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onNewGroupClick() {
        loadView("/com/example/projetjavafx/group/group-add-view.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) groupsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddMemberDialog(String groupName) {


        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Member to " + groupName);

        TextField searchUserField = new TextField();
        searchUserField.setPromptText("Search user...");

        ListView<String> userListView = new ListView<>();

        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            try (ResultSet resultSet = groupMemberModel.searchUsers(newValue)) {
                userListView.getItems().clear();
                while (resultSet != null && resultSet.next()) {
                    String username = resultSet.getString("username");
                    userListView.getItems().add(username);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        TextField roleField = new TextField();
        roleField.setPromptText("Enter role...");
        roleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) { // Limite à 50 caractères
                roleField.setText(oldValue); // Rejeter la saisie si elle dépasse la limite
            }
        });

        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            String role = roleField.getText();
            if (selectedUser != null && !role.isEmpty()) {
                int groupId = getGroupIdByName(groupName);
                int userId = getUserIdByUsername(selectedUser);

                if (groupId != -1 && userId != -1) {
                    boolean success = groupMemberModel.addMemberToGroup(groupId, userId, role);
                    if (success) {
                        showAlert("Success", "Member added successfully.");
                        dialog.close();
                    } else {
                        showAlert("Error", "Failed to add member.");
                    }
                }
            } else {
                showAlert("Error", "Please select a user and enter a role.");
            }
        });

        VBox dialogVBox = new VBox(10, searchUserField, userListView, roleField, addButton);
        dialog.getDialogPane().setContent(dialogVBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }



    private int getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM Users WHERE username = ?";
        try (Connection connection = AivenMySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showGroupMembers(String groupName) {
        try {
            // Récupérer l'ID du groupe
            int groupId = getGroupIdByName(groupName);
            if (groupId == -1) {
                showAlert("Erreur", "Groupe introuvable.");
                return;
            }

            // Charger la vue FXML pour afficher les membres
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/group/group-members-view.fxml"));
            Parent root = loader.load();

            // Passer les données au contrôleur de la vue des membres
            GroupMembersController controller = loader.getController();
            controller.setGroupId(groupId); // Passer l'ID du groupe
            controller.loadMembers(); // Charger les membres

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Membres du groupe : " + groupName);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les membres du groupe.");
        }
    }


    private void joinGroup(String groupName) {
        // Récupérer l'ID de l'utilisateur connecté
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            showAlert("Erreur", "Utilisateur non connecté.");
            return;
        }

        // Vérifier si l'utilisateur existe dans la base de données
        if (!userExists(userId)) {
            showAlert("Erreur", "L'utilisateur n'existe pas dans la base de données.");
            return;
        }

        // Récupérer l'ID du groupe
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            showAlert("Erreur", "Groupe introuvable.");
            return;
        }

        // Vérifier si le groupe existe dans la base de données
        if (!groupExists(groupId)) {
            showAlert("Erreur", "Le groupe n'existe pas dans la base de données.");
            return;
        }

        // Ajouter l'utilisateur au groupe avec un rôle par défaut (par exemple, "Membre")
        boolean success = groupMemberModel.addMemberToGroup(groupId, userId, "Membre");
        if (success) {
            showAlert("Succès", "Vous avez rejoint le groupe avec succès.");
        } else {
            showAlert("Erreur", "Impossible de rejoindre le groupe.");
        }
    }

    private boolean userExists(int userId) {
        String query = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
        try (Connection connection = AivenMySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean groupExists(int groupId) {
        String query = "SELECT COUNT(*) FROM UserGroups WHERE group_id = ?";
        try (Connection connection = AivenMySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private int getGroupIdByName(String groupName) {
        String query = "SELECT group_id FROM UserGroups WHERE name = ?";
        try (Connection connection = AivenMySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("group_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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