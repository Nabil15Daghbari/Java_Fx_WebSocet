package com.example.projetjavafx.root.group;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.sql.ResultSet;

public class GroupAddMemberController {
    @FXML
    private TextField searchUserField;

    @FXML
    private ListView<String> userListView;

    private SearchMemberRepository userModel = new SearchMemberRepository();
    private GroupAddMemberRepository groupMembersModel = new GroupAddMemberRepository();
    private int currentGroupId;

    public void setCurrentGroupId(int groupId) {
        this.currentGroupId = groupId;
    }

    @FXML
    private void onSearchUserClick() {
        String searchText = searchUserField.getText();
        if (searchText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom ou un email pour la recherche.");
            return;
        }

        ResultSet resultSet = userModel.searchUsers(searchText);
        if (resultSet == null) {
            showAlert("Erreur", "Problème de connexion à la base de données.");
            return;
        }

        userListView.getItems().clear();
        try {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                int userId = resultSet.getInt("user_id");

                userListView.getItems().add(username + " (" + email + ") - ID: " + userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddUserToGroupClick() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur.");
            return;
        }

        int userId = Integer.parseInt(selectedUser.split(" - ID: ")[1]);

        if (currentGroupId <= 0) {
            showAlert("Erreur", "Aucun groupe sélectionné.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Role");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the role for the new member:");

        dialog.showAndWait().ifPresent(role -> {
            boolean success = groupMembersModel.addMemberToGroup(currentGroupId, userId, role);
            if (success) {
                showAlert("Succès", "Utilisateur ajouté au groupe avec succès.");
            } else {
                showAlert("Erreur", "Échec de l'ajout de l'utilisateur au groupe.");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}