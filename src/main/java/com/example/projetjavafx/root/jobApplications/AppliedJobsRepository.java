package com.example.projetjavafx.root.jobApplications;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.organizer.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppliedJobsRepository {

    public static List<Job> getJobsAppliedByUser(int userId) throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT j.job_id, j.job_title, j.event_title, j.job_location, j.employment_type, " +
                "j.application_deadline, j.min_salary, j.max_salary, j.currency, j.job_description, " +
                "j.recruiter_name, j.recruiter_email, j.created_at " +
                "FROM jobs j JOIN Applications a ON j.job_id = a.job_id " +
                "WHERE a.user_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Job job = new Job(
                        rs.getInt("job_id"),
                        rs.getString("job_title"),
                        rs.getString("event_title"),
                        rs.getString("job_location"),
                        rs.getString("employment_type"),
                        rs.getString("application_deadline"),
                        rs.getDouble("min_salary"),
                        rs.getDouble("max_salary"),
                        rs.getString("currency"),
                        rs.getString("job_description"),
                        rs.getString("recruiter_name"),
                        rs.getString("recruiter_email"),
                        rs.getString("created_at")
                );
                jobs.add(job);
            }
        }
        return jobs;
    }

    public static String getApplicationStatusForJob(int userId, int jobId) throws SQLException {
        String sql = "SELECT status FROM Applications WHERE user_id = ? AND job_id = ?";
        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, jobId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            } else {
                return "Unknown"; // Or handle this case as needed.
            }
        }
    }

}
