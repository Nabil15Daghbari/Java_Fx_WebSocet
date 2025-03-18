package com.example.projetjavafx.root.messagerie.controllers;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.messagerie.WebSockets.ChatServer;
import com.example.projetjavafx.root.messagerie.WebSockets.Client;
import com.example.projetjavafx.root.messagerie.models.Message;
import com.example.projetjavafx.root.messagerie.repository.MessageDB;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserchatroomController {

    @FXML public Button button_send;
    @FXML public Button button_back;
    @FXML private TextField tf_message;
    @FXML private ScrollPane sp_main;
    @FXML private VBox vbox_messages;
    @FXML private Label label_conversation;
    private Client webSocketClient;
    private String username;
    private ChatServer server;
    private int currentUserId ;
    private int receiverUserId;

    private MessageDB messageDB = new MessageDB();
    private Set<String> displayedMessages = new HashSet<>();
    private boolean userScrolledUp = false;

    public void setUsername(String username) {
        this.username = username;
        label_conversation.setText("Conversation avec " + username);
    }

    public void setServer(ChatServer server) {
        this.server = server;
    }
    public void setWebSocketClient(Client webSocketClient) {
        this.webSocketClient = webSocketClient;


    }
    public void setUserIds(int currentUserId, int receiverUserId) {
        this.currentUserId = currentUserId;
        this.receiverUserId = receiverUserId;
    }

    @FXML
    public void initialize() {
        button_send.setOnAction(event -> sendMessage());
        button_back.setOnAction(event -> goBack());
        initializeWebSocket();

        loadPreviousMessages();
        startMessageUpdater();

        sp_main.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            userScrolledUp = newValue.doubleValue() < 1.0;
        });
    }
    public void markMessageAsDeleted(int messageId) {
        for (Node node : vbox_messages.getChildren()) {
            if (node instanceof HBox) {
                HBox messageContainer = (HBox) node;
                for (Node child : messageContainer.getChildren()) {
                    if (child instanceof Text && ((Text) child).getText().contains("id:" + messageId)) {
                        ((Text) child).setText("🗑️ Message supprimé");
                    }
                }
            }
        }
    }
    public void sendMessage() {
        String message = tf_message.getText().trim();
        if (!message.isEmpty()) {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                // Tu peux générer un messageId, par exemple avec un compteur ou en récupérant depuis la base de données
                int messageId = generateMessageId(); // Cette méthode doit être définie pour générer ou récupérer un ID unique

                String formattedMessage = currentUserId + ":user:" + receiverUserId + ":" + message;
                webSocketClient.send(formattedMessage);

                // Envoie le message avec le messageId
                addMessageToUI(messageId, currentUserId, "Moi: " + message);

                tf_message.clear();
            } else {
                showError("Connexion au serveur perdue, impossible d'envoyer le message.");
            }
        }
    }

    // Méthode pour générer un messageId (exemple avec compteur)
    private int generateMessageId() {
        // Tu pourrais générer un ID unique, par exemple en utilisant un compteur ou en récupérant l'ID de la base de données
        return (int) (System.currentTimeMillis() / 1000); // Utilise le timestamp comme messageId temporaire
    }


    public void displayMessage(String message) {
        Platform.runLater(() -> {
            if (!message.contains(":")) return;

            String[] parts = message.split(":", 4); // Divise le message en 4 parties : type, senderId, receiverId, content
            if (parts.length < 4) return; // Si le message ne contient pas 4 parties, ignorez-le

            try {
                String type = parts[0]; // "private" ou "group"
                int senderId = Integer.parseInt(parts[1]); // Récupère l'ID de l'expéditeur
                int targetId = Integer.parseInt(parts[2]); // Récupère l'ID du destinataire ou du groupe
                String content = parts[3]; // Contenu du message

                int messageId = 0; // Tu peux générer ou récupérer l'ID du message depuis la base de données

                String senderName = (senderId == currentUserId) ? "Moi" : "Lui"; // Affiche "Moi" ou "Lui" en fonction de l'expéditeur
                addMessageToUI(messageId, senderId, senderName + ": " + content); // Appelle ta méthode pour afficher le message
                scrollToBottom(); // Fais défiler vers le bas de la conversation
            } catch (NumberFormatException e) {
                showError("Erreur de conversion d'ID dans le message : " + message);
            }
        });
    }



    private void addMessageToUI(int messageId, int senderId, String content) {
        String messageKey = senderId + ":" + content;
        if (!displayedMessages.contains(messageKey)) {
            HBox messageContainer = new HBox();
            messageContainer.setPadding(new Insets(5, 10, 5, 10));

            Text text = new Text(content);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setMaxWidth(300);
            textFlow.setPadding(new Insets(8));
            textFlow.setStyle("-fx-background-radius: 15; -fx-padding: 10;");

            // Associe l'ID du message pour pouvoir le retrouver plus tard
            messageContainer.setUserData(messageId);

            if (senderId == currentUserId) {
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                textFlow.setStyle("-fx-background-color: #0084ff; -fx-text-fill: white;");

                // Ajout du bouton de suppression
                Button deleteButton = new Button("❌");
                deleteButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setOnAction(event -> {
                    if (webSocketClient != null) { // Vérifier que le client WebSocket est bien défini
                        webSocketClient.send("delete:" + messageId); // Envoie une requête de suppression au serveur
                    }
                });

                messageContainer.getChildren().addAll(textFlow, deleteButton);

            } else {
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                textFlow.setStyle("-fx-background-color: #00C853; -fx-text-fill: white;");
                messageContainer.getChildren().add(textFlow);
            }

            vbox_messages.getChildren().add(messageContainer);
            displayedMessages.add(messageKey);
            scrollToBottom(); // Assure le scroll automatique
        }
    }



    private void loadPreviousMessages() {
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT message_id, sender_id, content FROM Messages WHERE " +
                             "(sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp")) {

            stmt.setInt(1, currentUserId);
            stmt.setInt(2, receiverUserId);
            stmt.setInt(3, receiverUserId);
            stmt.setInt(4, currentUserId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                int senderId = rs.getInt("sender_id");
                String content = rs.getString("content");
                addMessageToUI(messageId, senderId, content);
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des messages : " + e.getMessage());
        }
    }


    private void startMessageUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            checkForNewMessages();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void checkForNewMessages() {
        try {
            List<Message> newMessages = messageDB.getNewMessages(currentUserId, receiverUserId, LocalDateTime.now().minusSeconds(1));
            for (Message message : newMessages) {
                int messageId = message.getMessageId(); // Assure-toi que tu as un champ messageId dans ta classe Message
                addMessageToUI(messageId, message.getSenderId(), message.getContent());
            }
        } catch (SQLException e) {
            showError("Erreur lors de la vérification des nouveaux messages : " + e.getMessage());
        }
    }


    private void scrollToBottom() {
        Platform.runLater(() -> {
            vbox_messages.layout();
            if (sp_main != null && !userScrolledUp) {
                sp_main.setVvalue(1.0);
            }
        });

    }
    public void removeMessageFromUI(int messageId) {
        Platform.runLater(() -> {
            vbox_messages.getChildren().removeIf(node -> node.getUserData() != null &&
                    node.getUserData().equals(messageId));
        });
    }


    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    public void initializeWebSocket() {
        new Thread(() -> {
            try {
                System.out.println("🛠️ Initialisation WebSocket avec CurrentUserId: " + currentUserId + ", ReceiverUserId: " + receiverUserId);
                String wsUrl = "ws://localhost:8082?userId=" + currentUserId + "&receiverId=" + receiverUserId;

                webSocketClient = new Client(wsUrl, currentUserId, this, null);
                webSocketClient.connect();

                Platform.runLater(() -> {
                    if (button_send.getScene() != null) {
                        Stage stage = (Stage) button_send.getScene().getWindow();
                        stage.setOnHiding(e -> webSocketClient.close());
                    } else {
                        showError("Erreur : La scène n'est pas encore initialisée.");
                    }
                });
            } catch (URISyntaxException e) {
                Platform.runLater(() -> showError("Erreur WebSocket : " + e.getMessage()));
            }
        }).start();
    }


    public void goBack() {
        Stage stage = (Stage) button_back.getScene().getWindow();
        stage.close();
    }


}
