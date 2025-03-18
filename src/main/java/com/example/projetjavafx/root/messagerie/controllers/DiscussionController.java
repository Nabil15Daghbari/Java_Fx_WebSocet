package com.example.projetjavafx.root.messagerie.controllers;

import com.example.projetjavafx.root.auth.SessionManager;
import com.example.projetjavafx.root.messagerie.WebSockets.Client;
import com.example.projetjavafx.root.messagerie.models.User;
import com.example.projetjavafx.root.messagerie.repository.GroupDB;
import com.example.projetjavafx.root.messagerie.repository.UserDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DiscussionController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> userList;
    @FXML
    private Button retourButton; // Référence au bouton Retour (optionnel)
    public Label welcomeText;
    public ListView startConversation;
    int currentUserId = SessionManager.getInstance().getCurrentUserId();
    private Client webSocketClient;

  boolean isLoggedIn = !SessionManager.getInstance().isUserNotConnected().getValue();
    private GroupDB groupDB = new GroupDB();  // Instance du repository
    private UserDB userDB = new UserDB();  // Instance pour accéder aux méthodes de UserDB

    @FXML
    private void loadView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            welcomeText.setText("Error loading view: " + e.getMessage());
        }
    }
    @FXML
    protected void onRetourClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/root/root-view.fxml", event);
    }



    @FXML
    private void searchUsersAndGroups() {
        String searchTerm = searchField.getText().trim();
        userList.getItems().clear(); // Vider la liste

        // 🔹 Utilisation de UserDB
        List<String> users = userDB.searchUsers(searchTerm, currentUserId);
        // 🔹 Utilisation de GroupDB pour chercher les groupes rejoints
        List<String> groups = groupDB.searchGroups(currentUserId, searchTerm);

        if (users.isEmpty() && groups.isEmpty()) {
            userList.getItems().add("❌ Aucun résultat trouvé !");
        } else {
            if (!users.isEmpty()) {
                userList.getItems().addAll(users);
            }
            if (!groups.isEmpty()) {
                userList.getItems().addAll(groups);
            }
        }
    }

    @FXML
private void startConversation(MouseEvent event) {
    String selectedItem = userList.getSelectionModel().getSelectedItem();
    if (selectedItem == null || selectedItem.startsWith("🔹") || selectedItem.startsWith("🟢")) return;
        System.out.println("➡️ Chat sélectionné avec : " + selectedItem);

    if (selectedItem.startsWith("👥")) {
        openGroupChatroom(selectedItem.substring(2)); // Retirer "👥 " et ouvrir chat groupe
    } else {
        openUserChatroom(selectedItem); // Ouvrir chat utilisateur
    }
}
    private void openGroupChatroom(String groupName) {
        int groupId = groupDB.getGroupIdByName(groupName);

       // loadGroupDetails(groupId); // Charger le nom et l’image du groupe

        if (groupId == -1) {
            System.out.println("❌ Impossible de trouver l'ID du groupe " + groupName);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/messagerie/groupchatroom/groupchatroom.fxml"));
            Parent root = loader.load();
            GroupChatroomController controller = loader.getController();

            // 🔹 Récupérer le vrai nom du groupe
            String realGroupName = groupDB.getGroupNameById(groupId);

            // 🔹 Passer les infos au contrôleur
            controller.initialize(currentUserId, realGroupName, groupId, webSocketClient);
            controller.loadGroupConversation(groupId);

            Stage stage = new Stage();
            stage.setTitle("Groupe : " + realGroupName);
            stage.setScene(new Scene(root));

            new Thread(() -> {
                try {
                    String websocketUrl = "ws://localhost:8082";
                    URI uri = new URI(websocketUrl);
                    Client client = new Client(websocketUrl, currentUserId, null, controller);
                    client.connect();
                    controller.setWebSocketClient(client);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("❌ Problème WebSocket !");
                }
            }).start();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du chargement de GroupChatroom.fxml !");
        }
    }



    private void openUserChatroom(String username) {
        int receiverUserId = userDB.getUserIdByUsername(username); // 🔹 Utilisation de UserDB
        System.out.println("ℹ️ ID du destinataire récupéré : " + receiverUserId);

        if (receiverUserId == -1) {
            System.out.println("❌ Impossible de trouver l'utilisateur " + username);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/messagerie/userchatroom/userchatroom.fxml"));
            Parent root = loader.load();
            // Récupérer le contrôleur du chatroom
            UserchatroomController controller = loader.getController();
            controller.setUsername(username);
            controller.setUserIds(currentUserId, receiverUserId);
            System.out.println("✅ Chatroom ouverte - CurrentUserId: " + currentUserId + ", ReceiverUserId: " + receiverUserId);

            // Ouvrir une nouvelle fenêtre pour le chatroom
            Stage stage = new Stage();
            stage.setTitle("Chat avec " + username);
            stage.setScene(new Scene(root));
            // Exécuter la connexion WebSocket dans un THREAD SÉPARÉ
            new Thread(() -> {
                try {
                    String websocketUrl = "ws://localhost:8082";
                    URI uri = new URI(websocketUrl);
                    Client client = new Client(websocketUrl, currentUserId, controller, null); // Passer null pour GroupChatroomController
                    client.connect();
                    controller.setWebSocketClient(client); // Assurer que le WebSocket est bien stocké

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("❌ Problème WebSocket !");
                }
            }).start();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du chargement de UserChatroom.fxml !");
        }
    }


}