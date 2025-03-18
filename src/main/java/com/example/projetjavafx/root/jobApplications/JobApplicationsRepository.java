package com.example.projetjavafx.root.jobApplications;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobApplicationsRepository {

    public static List<Application> getApplicationsForUserPostedJobs(int userId) throws SQLException {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM Applications WHERE job_id IN (SELECT job_id FROM jobs WHERE user_id = ?)";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                applications.add(new Application(
                        rs.getInt("application_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("applied_at"),
                        rs.getInt("rewarded"),
                        rs.getInt("job_id"),
                        rs.getString("cover_letter"),
                        rs.getString("resume_path")
                ));
            }
        }
        return applications;
    }

    public static void updateApplicationStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE Applications SET status = ? WHERE application_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, applicationId);
            pstmt.executeUpdate();
        }
    }
}
