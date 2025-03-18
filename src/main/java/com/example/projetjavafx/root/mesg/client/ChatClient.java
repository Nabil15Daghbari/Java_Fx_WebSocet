package com.example.projetjavafx.root.mesg.client;

import java.net.URI;
import java.net.URISyntaxException;

import com.example.projetjavafx.root.mesg.model.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import javafx.application.Platform;

public class ChatClient extends WebSocketClient {

    private ChatController controller;
    private int currentUserId;
    private Gson gson = new Gson();
    private boolean isConnected = false;

    public ChatClient(String serverUri, ChatController controller, int userId) throws URISyntaxException {
        super(new URI(serverUri));
        this.controller = controller;
        this.currentUserId = userId;

        // Configuration des options de connexion
        this.setConnectionLostTimeout(30); // 30 secondes
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
        isConnected = true;

        // Register user with the server
        try {
            Message registerMsg = new Message("REGISTER", currentUserId, 0, "");
            send(gson.toJson(registerMsg));
            System.out.println("Sent registration message for user ID: " + currentUserId);
        } catch (Exception e) {
            System.err.println("Error sending registration message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        try {
            final Message msg = gson.fromJson(message, Message.class);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                controller.displayMessage(msg);
            });
        } catch (Exception e) {
            System.err.println("Error processing received message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason + " (code: " + code + ", remote: " + remote + ")");
        isConnected = false;
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error in WebSocket connection: " + ex.getMessage());
        ex.printStackTrace();
    }

    public void sendMessage(int recipientId, String content) {
        if (!isConnected) {
            System.err.println("Cannot send message: not connected to server");
            throw new IllegalStateException("Not connected to server");
        }

        try {
            Message msg = new Message("MESSAGE", currentUserId, recipientId, content);
            String jsonMessage = gson.toJson(msg);
            System.out.println("Sending message: " + jsonMessage);
            send(jsonMessage);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public boolean isConnected() {
        return isConnected && isOpen();
    }
}