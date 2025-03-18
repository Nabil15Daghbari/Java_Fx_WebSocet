package com.example.projetjavafx.root.events;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.auth.SessionManager;
import com.mysql.cj.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class EventsRepository {

    // Récupère la liste des catégories depuis la table Categories
    public static ObservableList<String> getCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.add("All"); // Option par défaut
        String sql = "SELECT category_id, name FROM Categories";
        try (Connection connection = AivenMySQLManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {
            while (resultSet.next()) {
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("name");
                categories.add(categoryId + " - " + categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }


    public static void participateInEvent (int eventId){
        String sql ="INSERT INTO participation (event_id, participant_id) VALUES (?, ?)";

        int currentUser = SessionManager.getInstance().getCurrentUserId();

        try (Connection connection = AivenMySQLManager.getConnection()) {
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, eventId);
        statement.setInt(2, currentUser);
        statement.executeUpdate();
        SessionManager.getInstance().updatepart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Récupère la liste des événements en fonction d'un texte de recherche et d'un filtre de catégorie
    public static ObservableList<Event> getEvents(String searchText, String category) {
        ObservableList<Event> events = FXCollections.observableArrayList();
        String query = "SELECT e.*, c.name AS category_name " +
                "FROM Events e " +
                "LEFT JOIN Categories c ON e.category_id = c.category_id " +
                "WHERE 1=1 ";
        if (searchText != null && !searchText.isEmpty()) {
            query += "AND (LOWER(e.name) LIKE ? OR LOWER(e.description) LIKE ?) ";
        }
        if (category != null && !category.equals("All")) {
            query += "AND e.category_id = ? ";
        }
        query += " GROUP BY e.event_id, e.name, e.description, e.start_time, e.end_time, e.location, e.image, c.name";

        try (Connection connection = AivenMySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            int paramIndex = 1;
            if (searchText != null && !searchText.isEmpty()) {
                String searchPattern = "%" + searchText.toLowerCase() + "%";
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
            }
            if (category != null && !category.equals("All")) {
                int categoryId = Integer.parseInt(category.split(" - ")[0]);
                statement.setInt(paramIndex++, categoryId);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.setEventId(resultSet.getInt("event_id"));
                event.setName(resultSet.getString("name"));
                event.setDescription(resultSet.getString("description"));
                // Ici, on récupère directement les dates sous forme de String
                event.setStartDate(resultSet.getString("start_time"));
                event.setEndDate(resultSet.getString("end_time"));
                event.setLocation(resultSet.getString("location"));
                event.setImageBase64(resultSet.getString("image"));
                event.setCategoryName(resultSet.getString("category_name"));

                if (SessionManager.getInstance().getCurrentUserId() != resultSet.getInt("organizer_id")) {
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
}
