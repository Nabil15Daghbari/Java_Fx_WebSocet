package com.example.projetjavafx.root.messagerie.repository;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.messagerie.models.User;
import com.example.projetjavafx.root.messagerie.models.UserMessages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDB {


    public int getUserIdByUsername(String username) {
        int userId = -1;
        String query = "SELECT user_id FROM Users WHERE username = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("user_id");  // Récupérer l'ID du destinataire
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }


    public List<String> searchUsers(String searchTerm, int currentUserId) {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM Users WHERE username LIKE ? AND user_id != ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setInt(2, currentUserId);  // Exclure l'utilisateur actuel

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<UserMessages> getRecentContactsWithLastMessage(int currentUserId) {
        // Requête SQL pour récupérer les contacts récents avec leur dernier message
        String query = "SELECT u.user_id, u.username, m.content, m.timestamp " +
                "FROM Users u " +
                "JOIN (" +
                "  SELECT CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END AS contact_id, " +
                "         content, timestamp, " +
                "         ROW_NUMBER() OVER (PARTITION BY CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END ORDER BY timestamp DESC) AS rn " +
                "  FROM Messages " +
                "  WHERE sender_id = ? OR receiver_id = ?" +
                ") m ON u.user_id = m.contact_id " +
                "WHERE m.rn = 1 AND u.user_id != ? " +
                "ORDER BY m.timestamp DESC";

        List<UserMessages> contacts = new ArrayList<>();

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Paramètres de la requête
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, currentUserId);
            stmt.setInt(3, currentUserId);
            stmt.setInt(4, currentUserId);
            stmt.setInt(5, currentUserId);

            // Exécution de la requête
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String lastMessage = rs.getString("content");
                LocalDateTime sentAt = rs.getTimestamp("timestamp").toLocalDateTime();

                // Ajout du contact à la liste
                contacts.add(new UserMessages(userId, username, lastMessage, sentAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }
    //getlastMessagewithuser
    public UserMessages getLastMessageWithUser(int userId, int currentUserId) throws SQLException {
        String sql = "SELECT u.user_id, u.username, m.content, m.timestamp " +
                "FROM Users u " +
                "JOIN Messages m ON (u.user_id = m.sender_id OR u.user_id = m.receiver_id) " +
                "WHERE (m.sender_id = ? OR m.receiver_id = ?) " +
                "AND u.user_id != ? " +
                "ORDER BY m.timestamp DESC " +
                "LIMIT 1";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, currentUserId);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserMessages(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("content"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }

}