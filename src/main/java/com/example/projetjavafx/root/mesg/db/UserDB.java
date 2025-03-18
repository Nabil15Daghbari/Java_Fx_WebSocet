package com.example.projetjavafx.root.mesg.db;


import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.mesg.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, status FROM Users";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(rs.getInt("user_id"), rs.getString("username"));
               // user.setStatus(rs.getString("status"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, status FROM Users WHERE username LIKE ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(rs.getInt("user_id"), rs.getString("username"));
             //   user.setStatus(rs.getString("status"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void updateUserStatus(int userId, String status) {
        String query = "UPDATE Users SET status = ? WHERE user_id = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

