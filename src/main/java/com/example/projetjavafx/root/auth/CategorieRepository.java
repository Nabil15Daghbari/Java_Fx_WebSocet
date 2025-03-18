package com.example.projetjavafx.root.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CategorieRepository {
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://mysql-1dcac8df-moamedsalahsaoudi123-c05d.e.aivencloud.com:22451/projet_integre_db?ssl-mode=REQUIRED";
        String user = "avnadmin";
        String password = "AVNS_5qB58jyOaJs3WW0eYS9";
        return DriverManager.getConnection(url, user, password);
    }

    public int getCategoryId(String categoryName) throws SQLException {
        String query = "SELECT category_id FROM Categories WHERE name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("category_id");
            }
        }
        return -1;  // Retourne -1 si la catégorie n'existe pas
    }

    public void insertUserInterest(int userId, int categoryId) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM UserInterests WHERE user_id = ? AND category_id = ?";
        String insertSql = "INSERT INTO UserInterests (user_id, category_id) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            // Vérifie si la catégorie est déjà enregistrée pour cet utilisateur
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, categoryId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("L'utilisateur a déjà cette catégorie : " + categoryId);
                return; // Ne pas insérer de doublon
            }

            // Insérer seulement si ce n'est pas déjà enregistré
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, categoryId);
            insertStmt.executeUpdate();
        }
    }
}
