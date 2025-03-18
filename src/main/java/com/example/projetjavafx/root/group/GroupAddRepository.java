package com.example.projetjavafx.root.group;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GroupAddRepository {

    public boolean saveGroup(String name, String description, String rules, String imageUrl) {
        String query = "INSERT INTO UserGroups (name, description, rules, profile_picture) VALUES (?, ?, ?, ?)";
        try (Connection connection = AivenMySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, rules);
            statement.setString(4, imageUrl); // Stockez l'URL de l'image

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
