package com.example.projetjavafx.root.messagerie.WebSockets;

import com.example.projetjavafx.root.messagerie.controllers.GroupChatroomController;
import com.example.projetjavafx.root.messagerie.controllers.UserchatroomController;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class Client extends WebSocketClient {

    private final int userId; // 👈 Utilisez final pour l'immutabilité
    private final UserchatroomController userController;
    private final GroupChatroomController groupController;

    public Client(String serverUri, int userId, UserchatroomController userController, GroupChatroomController groupController) throws URISyntaxException {
        super(new URI(serverUri));
        this.userId = userId;
        this.userController = userController;
        this.groupController = groupController; // Ne pas mettre null ici, il faut pouvoir gérer les deux.
    }


    @Override
    public void onOpen(ServerHandshake handshake) {
        send("login:" + this.userId);
    }
    @Override
    public void onMessage(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("deleted:")) {
                int messageId = Integer.parseInt(message.split(":")[1]);
                userController.removeMessageFromUI(messageId); // Fonction à ajouter dans UserchatroomController
            } else if (message.startsWith("group:")) {
                String[] parts = message.split(":", 4);
                if (parts.length == 4) {
                    int groupId = Integer.parseInt(parts[1]);
                    int senderId = Integer.parseInt(parts[2]);
                    String content = parts[3];
                    groupController.displayGroupMessage(groupId, senderId, content);
                }
            } else {
                // Format : "private/senderId:receiverId:messageId:messageContent"
                String[] parts = message.split(":", 4);
                if (parts.length == 4) {
                    int senderId = Integer.parseInt(parts[1]);
                    int receiverId = Integer.parseInt(parts[2]);
                    int messageId = Integer.parseInt(parts[3]);
                    String content = parts[4];

                    userController.displayMessage(content); // Ajoute messageId pour l'affichage
                }
            }
        });
    }





    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ Déconnecté : " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Platform.runLater(() ->
                userController.showError("Erreur WebSocket: " + ex.getMessage())
        );
        System.out.println("⚠️ Erreur WebSocket : " + ex.getMessage());
    }
}
