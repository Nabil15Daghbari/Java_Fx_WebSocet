package com.example.projetjavafx.root.organizer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.projetjavafx.root.auth.SessionManager;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;

public class AnalyticsRepository {

    public static EventStats getEventStats(int organizerId) throws SQLException {
        List<EventStats> stats = new ArrayList<>();
        String sql = "SELECT e.event_id, e.name, "
                + "COUNT(p.id) AS total, "
                + "SUM(CASE WHEN u.gender = 'Male' THEN 1 ELSE 0 END) AS male, "
                + "SUM(CASE WHEN u.gender = 'Female' THEN 1 ELSE 0 END) AS female "
                + "FROM Events e "
                + "LEFT JOIN participation p ON e.event_id = p.event_id "
                + "LEFT JOIN Users u ON p.participant_id = u.user_id "
                + "WHERE e.organizer_id = ? "
                + "AND e.event_id = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, SessionManager.getInstance().getCurrentUserId());
            pstmt.setInt(2,62);
            System.out.println("Executing getEventStats query with organizerId: " + organizerId);
            ResultSet rs = pstmt.executeQuery();
            int total =0;
            int male = 0;
            int female = 0 ;
            String name ="";
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                 name = rs.getString("name");
                 total = rs.getInt("total");
                 male = rs.getInt("male");
                 female = rs.getInt("female");
                System.out.println("Row: event_id=" + eventId + ", name=" + name
                        + ", total=" + total + ", male=" + male + ", female=" + female);

            }
        return new EventStats(62, name, total, male, female);
        }

    }
    public static List<EventAgeStats> getEventAvgAgeStats() throws SQLException {
        List<EventAgeStats> ret = new ArrayList<>();;
        String sql = "SELECT e.event_id, e.name, AVG(u.age) AS avg_age " +
                "FROM Events e " +
                "LEFT JOIN participation p ON e.event_id = p.event_id " +
                "LEFT JOIN Users u ON p.participant_id = u.user_id " +
                "WHERE e.organizer_id = 1 " +
                "GROUP BY e.event_id";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {




            ResultSet rs = pstmt.executeQuery();

            String name = "";
            double avgAge = 0.0;
            // Assuming that the query returns one row per event
            while(rs.next()) {
                int retEventId = rs.getInt("event_id");
                name = rs.getString("name");
                avgAge = rs.getDouble("avg_age");
                System.out.println("Row: event_id=" + retEventId + ", name=" + name
                        + ", avg_age=" + avgAge);
                EventAgeStats  stat = new EventAgeStats(retEventId,name,avgAge);
                ret.add(stat);
                System.out.println(stat.toString());
            }

            // Create and return an EventStats object with the average age data.
            // (Make sure your EventStats class has a constructor matching these parameters.)
            return ret;
        }
    }


}
