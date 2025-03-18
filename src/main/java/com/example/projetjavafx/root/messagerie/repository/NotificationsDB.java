package com.example.projetjavafx.root.messagerie.repository;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationsDB {
    private static final Logger LOGGER = Logger.getLogger(NotificationsDB.class.getName());

    public void createNotification(int receiverId, int senderId, int messageId) {
        String content = "Nouveau message de " + getUsernameById(senderId);
        String sql = "INSERT INTO Notifications (user_id, message_id, content, is_read, created_at) VALUES (?, ?, ?, 0, NOW())";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, receiverId);
            stmt.setInt(2, messageId);
            stmt.setString(3, content);
            stmt.executeUpdate();
            System.out.println("✅ Notification ajoutée pour l'utilisateur " + receiverId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la notification : " + e.getMessage(), e);
        }
    }
    public List<String> getUserNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT content FROM Notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(rs.getString("content"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des notifications : " + e.getMessage(), e);
        }
        return notifications;
    }
    public void deleteOldNotifications(int days) {
        String sql = "DELETE FROM Notifications WHERE created_at < NOW() - INTERVAL ? DAY";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("🗑 " + rowsDeleted + " anciennes notifications supprimées !");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression des notifications : " + e.getMessage(), e);
        }
    }

    public void markNotificationsAsRead(int receiverId, int senderId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE user_id = ? AND message_id IN " +
                "(SELECT message_id FROM Messages WHERE sender_id = ? AND receiver_id = ?)";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, receiverId);
            stmt.setInt(2, senderId);
            stmt.setInt(3, receiverId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Notifications marquées comme lues pour l'utilisateur " + receiverId);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des notifications : " + e.getMessage(), e);
        }
    }

    private String getUsernameById(int userId) {
        String sql = "SELECT username FROM Users WHERE user_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du nom d'utilisateur : " + e.getMessage(), e);
        }
        return null;
    }
    //partieeee  groupp
    public void createGroupMessageNotification(int senderId, int groupId, int messageId) {
        try {
            GroupDB groupDB = new GroupDB();
            String groupName = groupDB.getGroupNameById(groupId);
            if (groupName == null) {
                groupName = "Groupe inconnu"; // Sécurité pour éviter null
            }
            String content = "Nouveau message dans le groupe " + groupName;
            List<Integer> members = new GroupDB().getGroupMembers(groupId);

            String sql = "INSERT INTO Notifications (user_id, message_id, content, is_read, created_at) VALUES (?, ?, ?, 0, NOW())";

            try (Connection conn = AivenMySQLManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                for (int memberId : members) {
                    if (memberId != senderId) { // Ne pas notifier l'expéditeur
                        stmt.setInt(1, memberId);
                        stmt.setInt(2, messageId);
                        stmt.setString(3, content);
                        stmt.executeUpdate();
                        System.out.println("✅ Notification ajoutée pour l'utilisateur " + memberId);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout des notifications de groupe : " + e.getMessage(), e);
        }
    }


}
