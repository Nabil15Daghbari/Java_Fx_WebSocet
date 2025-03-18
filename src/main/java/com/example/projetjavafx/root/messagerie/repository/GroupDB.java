package com.example.projetjavafx.root.messagerie.repository;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.messagerie.models.Message;
import com.example.projetjavafx.root.messagerie.models.UserGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDB {
    /**
     * Récupère tous les membres d'un groupe donné.
     *
     * @param groupId L'ID du groupe.
     * @return Une liste des IDs des membres du groupe.
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données.
     */

    public List<Integer> getGroupMembers(int groupId) throws SQLException {
        List<Integer> members = new ArrayList<>();
        String sql = "SELECT user_id FROM GroupMembers WHERE group_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(rs.getInt("user_id"));
                }
            }
        }
        return members;
    }
    public void sendGroupMessage(int senderId, int groupId, String content) throws SQLException {
        String sql = "INSERT INTO Messages (sender_id, group_id, content, timestamp) VALUES (?, ?, ?, NOW())";
        int messageId = -1;

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, groupId);
            stmt.setString(3, content);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    messageId = generatedKeys.getInt(1);
                }
            }
        }

        if (messageId != -1) {
            new NotificationsDB().createGroupMessageNotification(senderId, groupId, messageId);
        }
    }

    public List<Message> getMessagesByGroup(int groupId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.username FROM Messages m " +
                "JOIN Users u ON m.sender_id = u.user_id " +
                "WHERE m.group_id = ? ORDER BY m.timestamp ASC";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("message_id"),
                            rs.getInt("sender_id"),
                            rs.getInt("group_id"),
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
//    public List<String> getGroupMessages(int groupId) throws SQLException {
//        List<String> messages = new ArrayList<>();
//        String sql = "SELECT sender_id, content FROM Messages WHERE group_id = ? ORDER BY timestamp DESC LIMIT 20";
//
//        try (Connection conn = AivenMySQLManager.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, groupId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    int senderId = rs.getInt("sender_id");
//                    String content = rs.getString("content");
//                    messages.add(senderId + ": " + content);
//                }
//            }
//        }
//        return messages;
//    }
    public void saveGroupMessage(Message message) throws SQLException {
        String sql = "INSERT INTO Messages (sender_id, group_id, content, timestamp) VALUES (?, ?, ?, NOW())";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId()); // Ici, receiverId représente le groupId
            stmt.setString(3, message.getContent());
            stmt.executeUpdate();
        }
    }

    public boolean isUserInGroup(int userId, int groupId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM GroupMembers WHERE group_id = ? AND user_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    public List<String> searchGroups(int userId, String searchTerm) {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT ug.name FROM UserGroups ug " +
                "JOIN GroupMembers gm ON ug.group_id = gm.group_id " +
                "WHERE gm.user_id = ? AND ug.name LIKE ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                groups.add("👥 " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }
    public UserGroup getGroupDetails(int groupId) {
        String sql = "SELECT name, profile_picture FROM UserGroups WHERE group_id = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserGroup(
                        groupId,
                        rs.getString("name"),
                        null,  // Pas besoin de la description ici
                        rs.getString("profile_picture"),
                        null,  // Pas besoin des règles
                        null   // Pas besoin de la date de création
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si le groupe n'existe pas
    }

    public String getGroupNameById(int groupId) {
        String sql = "SELECT name FROM UserGroups WHERE group_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si le groupe n'est pas trouvé
    }

    public int getGroupIdByName(String groupName) {
        String sql = "SELECT group_id FROM UserGroups WHERE name = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("group_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si le groupe n'est pas trouvé
    }

}
