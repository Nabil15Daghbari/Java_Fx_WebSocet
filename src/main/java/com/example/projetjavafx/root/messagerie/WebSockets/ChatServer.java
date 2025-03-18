package com.example.projetjavafx.root.messagerie.WebSockets;

import com.example.projetjavafx.root.messagerie.repository.MessageDB;
import com.example.projetjavafx.root.messagerie.repository.GroupDB;
import com.example.projetjavafx.root.messagerie.models.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServer extends WebSocketServer {
    private static final int PORT = 8082;
    private static final ConcurrentHashMap<Integer, WebSocket> userConnections = new ConcurrentHashMap<>();
    private static final MessageDB messageDB = new MessageDB();
    private static final GroupDB groupDB = new GroupDB();

    public ChatServer() {
        super(new InetSocketAddress(PORT));
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
        System.out.println("✅ Serveur WebSocket démarré sur le port " + PORT);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String query = handshake.getResourceDescriptor();
        int userId = extractUserId(query);

        if (userId > 0) {
            userConnections.put(userId, conn);
            System.out.println("✅ Utilisateur " + userId + " connecté.");
        } else {
            conn.close();
        }
    }

    private int extractUserId(String query) {
        Pattern pattern = Pattern.compile("userId=(\\d+)");
        Matcher matcher = pattern.matcher(query);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        userConnections.values().remove(conn);
        System.out.println("🔴 Un utilisateur s'est déconnecté.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message == null || message.trim().isEmpty()) return;

        String[] parts = message.split(":", 4);
        if (parts.length < 4) {
            System.out.println("❌ Message mal formé : " + message);
            return;
        }

        try {
            String type = parts[0]; // "private" ou "group"
            int senderId = Integer.parseInt(parts[1]);
            int targetId = Integer.parseInt(parts[2]);
            String content = parts[3];

            int messageId = generateMessageId();
            Message newMessage = new Message(messageId, senderId, targetId, content, LocalDateTime.now());

            if (type.equals("private")) {
                forwardMessage(targetId, newMessage);
                messageDB.saveMessage(newMessage);
            } else if (type.equals("group")) {
                forwardGroupMessage(targetId, newMessage);
                groupDB.saveGroupMessage(newMessage);
            }
        } catch (NumberFormatException | SQLException e) {
            System.out.println("❌ Erreur lors du traitement du message : " + e.getMessage());
        }
    }

    private int generateMessageId() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private void forwardGroupMessage(int groupId, Message message) {
        try {
            List<Integer> members = groupDB.getGroupMembers(groupId);
            for (int memberId : members) {
                if (memberId != message.getSenderId()) {
                    WebSocket recipientConn = userConnections.get(memberId);
                    if (recipientConn != null && recipientConn.isOpen()) {
                        recipientConn.send("group:" + groupId + ":" + message.getSenderId() + ":" + message.getContent());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des membres du groupe : " + e.getMessage());
        }
    }

    private void forwardMessage(int receiverId, Message message) {
        WebSocket receiverSocket = userConnections.get(receiverId);
        if (receiverSocket != null && receiverSocket.isOpen()) {
            receiverSocket.send("private:" + message.getSenderId() + ":" + receiverId + ":" + message.getContent());
        } else {
            System.out.println("Utilisateur " + receiverId + " non connecté.");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("✅ Serveur WebSocket prêt !");
    }
}
