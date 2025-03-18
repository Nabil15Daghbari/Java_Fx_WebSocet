package com.example.projetjavafx.root.group;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;

import java.sql.*;

public class GroupProfileRepository {
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://mysql-1dcac8df-moamedsalahsaoudi123-c05d.e.aivencloud.com:22451/projet_integre_db?ssl-mode=REQUIRED";
        String user = "avnadmin";
        String password = "AVNS_5qB58jyOaJs3WW0eYS9";
        return DriverManager.getConnection(url, user, password);
    }


    public ResultSet getGroups(String searchText) {
        String query = "SELECT name, description, rules, profile_picture FROM UserGroups";
        if (searchText != null && !searchText.isEmpty()) {
            query += " WHERE name LIKE ? OR description LIKE ?";
        }

        try {
            Connection connection = AivenMySQLManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            if (searchText != null && !searchText.isEmpty()) {
                String searchPattern = "%" + searchText.toLowerCase() + "%";
                statement.setString(1, searchPattern);
                statement.setString(2, searchPattern);
            }

            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
