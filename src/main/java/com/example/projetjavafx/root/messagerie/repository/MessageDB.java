package com.example.projetjavafx.root.messagerie.repository;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.messagerie.models.Message;
import com.example.projetjavafx.root.messagerie.models.UserMessages;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.example.projetjavafx.root.messagerie.repository.NotificationsDB;

public class MessageDB {
    private static final Logger LOGGER = Logger.getLogger(MessageDB.class.getName());

    public boolean sendMessage(int senderId, int receiverId, String content) {
        String sql = "INSERT INTO Messages (sender_id, receiver_id, content, timestamp) VALUES (?, ?, ?, NOW())";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Message envoyé avec succès !");

                // Récupérer l'ID du message inséré
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int messageId = rs.getInt(1);
                        //  Appeler la méthode createNotification depuis NotificationsDB
                        NotificationsDB notificationsDB = new NotificationsDB();
                        notificationsDB.createNotification(receiverId, senderId,messageId);
                    }
                }
                return true;
            } else {
                System.out.println("⚠ Aucun message inséré !");
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi du message : " + e.getMessage(), e);
            return false;
        }
    }


    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO Messages (sender_id, receiver_id, group_id, content, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setObject(3, message.getGroupId());
            stmt.setString(4, message.getContent());
            stmt.setTimestamp(5, Timestamp.valueOf(message.getTimestamp()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setMessageId(rs.getInt(1));
                }
            }
        }
    }

    public List<Message> getMessagesBetweenUsers(int user1, int user2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.username FROM Messages m " +
                "JOIN Users u ON m.sender_id = u.user_id " +
                "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
                "ORDER BY m.timestamp ASC";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("message_id"),
                            rs.getInt("sender_id"),
                            rs.getInt("receiver_id"),
                            rs.getString("content"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    message.setSenderUsername(rs.getString("username")); // ✅ Ajouter le username du sender
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    public List<Message> getNewMessages(int user1, int user2, LocalDateTime lastCheck) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Messages WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) AND timestamp > ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            stmt.setTimestamp(5, Timestamp.valueOf(lastCheck));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("message_id"),
                            rs.getInt("sender_id"),
                            rs.getInt("receiver_id"),
                            rs.getString("content"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    public boolean deleteMessage(int messageId) throws SQLException {
        String query = "UPDATE Messages SET deleted = 1 WHERE message_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, messageId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    private boolean isChatOpen(int receiverId, int senderId) {
        String sql = "SELECT MAX(timestamp) FROM Messages WHERE sender_id = ? AND receiver_id = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp lastMessageTime = rs.getTimestamp(1);
                if (lastMessageTime != null) {
                    return lastMessageTime.toLocalDateTime().isBefore(LocalDateTime.now().minusMinutes(5));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du chat ouvert : " + e.getMessage(), e);
        }
        return false; // Par défaut, on suppose que le chat n'est pas ouvert
    }

    public List<UserMessages> getUsersWithLastMessage(int userId) throws SQLException {
        List<UserMessages> userMessages = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, m.content, m.timestamp " +
                "FROM Users u " +
                "JOIN Messages m ON (u.user_id = m.sender_id OR u.user_id = m.receiver_id) " +
                "WHERE (m.sender_id = ? OR m.receiver_id = ?) AND u.user_id != ? " +
                "AND m.message_id = ( " +
                "    SELECT MAX(message_id) " +
                "    FROM Messages " +
                "    WHERE (sender_id = u.user_id AND receiver_id = ?) OR (sender_id = ? AND receiver_id = u.user_id) " +
                "    AND timestamp = ( " +
                "        SELECT MAX(timestamp) " +
                "        FROM Messages " +
                "        WHERE (sender_id = u.user_id AND receiver_id = ?) OR (sender_id = ? AND receiver_id = u.user_id) " +
                "    ) " +
                ") " +
                "ORDER BY m.timestamp DESC";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            stmt.setInt(6, userId);
            stmt.setInt(7, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserMessages userMessage = new UserMessages(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("content"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    userMessages.add(userMessage);
                }
            }
        }
        return userMessages;
    }


}