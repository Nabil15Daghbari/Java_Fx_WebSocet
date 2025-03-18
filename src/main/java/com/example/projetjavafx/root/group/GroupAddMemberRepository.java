package com.example.projetjavafx.root.group;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GroupAddMemberRepository {

    public ResultSet searchUsers(String searchText) {
        String query = "SELECT user_id, username FROM Users WHERE username LIKE ?";
        try {
            Connection connection = AivenMySQLManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchText + "%");
            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addMemberToGroup(int groupId, int userId, String role) {
        if (role.length() > 50) { // Vérification de la longueur du rôle
            System.err.println("Le rôle dépasse la taille maximale autorisée.");
            return false;
        }

        String query = "INSERT INTO GroupMembers (group_id, user_id, role) VALUES (?, ?, ?)";
        try {
            Connection connection = AivenMySQLManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groupId);
            statement.setInt(2, userId);
            statement.setString(3, role);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}