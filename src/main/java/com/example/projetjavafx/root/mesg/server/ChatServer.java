package com.example.projetjavafx.root.mesg.server;


import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.example.projetjavafx.root.mesg.model.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;

public class ChatServer extends WebSocketServer {

    private static final int PORT = 8887;
    private Map<Integer, WebSocket> userConnections = new HashMap<>();
    private Gson gson = new Gson();

    public ChatServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress());
        // Remove user from connections map
        userConnections.entrySet().removeIf(entry -> entry.getValue() == conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);

        try {
            Message msg = gson.fromJson(message, Message.class);

            switch (msg.getType()) {
                case "REGISTER":
                    // Register user connection
                    userConnections.put(msg.getSenderId(), conn);
                    System.out.println("Registered user: " + msg.getSenderId());

                    // Send confirmation back to client
                    Message confirmMsg = new Message("SYSTEM", 0, msg.getSenderId(),
                            "Connected to server successfully. Your user ID: " + msg.getSenderId());
                    conn.send(gson.toJson(confirmMsg));
                    break;

                case "MESSAGE":
                    // Forward message to recipient
                    WebSocket recipientConn = userConnections.get(msg.getRecipientId());
                    if (recipientConn != null && recipientConn.isOpen()) {
                        recipientConn.send(message);
                        System.out.println("Forwarded message to user: " + msg.getRecipientId());
                    } else {
                        // Store message for offline delivery or notify sender
                        conn.send(gson.toJson(new Message("SYSTEM", msg.getSenderId(), 0,
                                "User is offline. Message will be delivered when they connect.")));
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();

            // Send error message back to client
            try {
                conn.send(gson.toJson(new Message("SYSTEM", 0, 0,
                        "Error processing your message: " + e.getMessage())));
            } catch (Exception ex) {
                System.err.println("Error sending error message: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error with connection: " + (conn != null ? conn.getRemoteSocketAddress() : "null"));
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Chat server started on port: " + PORT);
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
        System.out.println("ChatServer started on port: " + PORT);

        // Keep the server running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}