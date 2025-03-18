package com.example.projetjavafx.root.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UsernameRepository {
    private Connection connect() throws SQLException {
    String url = "jdbc:mysql://mysql-1dcac8df-moamedsalahsaoudi123-c05d.e.aivencloud.com:22451/projet_integre_db?ssl-mode=REQUIRED";
    String user = "avnadmin";
    String pass = "AVNS_5qB58jyOaJs3WW0eYS9";
    return DriverManager.getConnection(url, user, pass);
}

    public boolean usernameExists(String email) {
        String query = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Nombre d'utilisateurs trouvés avec cet email : " + count);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
