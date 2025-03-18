package com.example.projetjavafx.root.messagerie.controllers;

import com.example.projetjavafx.root.messagerie.models.Message;
import com.example.projetjavafx.root.messagerie.models.UserGroup;
import com.example.projetjavafx.root.messagerie.repository.GroupDB;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.java_websocket.client.WebSocketClient;

import java.sql.SQLException;
import java.util.List;

public class GroupChatroomController {
    @FXML
    private ListView<String> chatListView;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendGroupButton;
    @FXML
    private ImageView groupImageView;
    @FXML
    private Label groupNameLabel;

    private GroupDB groupDB = new GroupDB(); // ✅ Utilisation de GroupDB
    private WebSocketClient client;
    private int groupId;
    private int userId;
    private String username; // ✅ Stocker le username

    public void initialize(int userId, String username, int groupId, WebSocketClient client) {
        this.userId = userId;
        this.username = username;
        this.groupId = groupId;
        this.client = client;

        // Appliquer un CellFactory pour personnaliser l'affichage
        chatListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("🔵 Moi:")) {
                        setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 10px;");
                    } else {
                        setStyle("-fx-background-color: #7ED321; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 10px;");
                    }
                }
            }
        });

        sendGroupButton.setOnAction(event -> sendMessage());
        loadGroupDetails(groupId); // Charger nom + image

        loadGroupConversation(groupId);
    }

    public void setWebSocketClient(WebSocketClient client) {
        this.client = client;
    }

    public void loadGroupConversation(int groupId) {
        this.groupId = groupId;
        chatListView.getItems().clear();

        try {
            List<Message> messages = groupDB.getMessagesByGroup(groupId);
            for (Message msg : messages) {
                chatListView.getItems().add(msg.getSenderUsername() + ": " + msg.getContent());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String content = messageField.getText().trim();
        if (!content.isEmpty()) {
            try {
                groupDB.sendGroupMessage(userId, groupId, content);
                String message = userId + ":group:" + groupId + ":" + content;
                client.send(message);

                Platform.runLater(() -> chatListView.getItems().add("Moi: " + content));
                messageField.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveMessage(String senderUsername, String content, int senderId) {
        chatListView.getItems().add(formatMessage(senderId, senderUsername, content));
    }

    private String formatMessage(int senderId, String senderUsername, String content) {
        if (senderId == userId) {
            return "🔵 Moi: " + content;
        } else {
            return "🟢 " + senderUsername + ": " + content;
        }
    }

    public void displayGroupMessage(int groupId, int senderId, String content) {
        Platform.runLater(() -> {
            String message = "Utilisateur " + senderId + ": " + content;
            chatListView.getItems().add(message);
        });
    }
    public void loadGroupDetails(int groupId) {
        UserGroup group = groupDB.getGroupDetails(groupId);
        if (group != null) {
            groupNameLabel.setText(group.getName());

            // Charger l'image du groupe si elle existe
            if (group.getProfilePicture() != null && !group.getProfilePicture().isEmpty()) {
                Image image = new Image(group.getProfilePicture());
                groupImageView.setImage(image);
            } else {
                groupImageView.setImage(new Image("/images/default-group.png")); // Image par défaut
            }
        } else {
            System.out.println("❌ Groupe introuvable !");
        }
    }
}
