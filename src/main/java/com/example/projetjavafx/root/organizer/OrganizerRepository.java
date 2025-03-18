package com.example.projetjavafx.root.organizer;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.auth.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerRepository {

    // Fetch all events for the organizer
    public static List<Map<String, String>> getOrganizerEvents(int userId) throws SQLException {
        List<Map<String, String>> events = new ArrayList<>();
        String sql = "SELECT name, description, start_time, end_time, location FROM Events WHERE organizer_id = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, SessionManager.getInstance().getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, String> event = new HashMap<>();
                event.put("name", rs.getString("name"));
                event.put("description", rs.getString("description"));
                event.put("start_time", rs.getString("start_time"));
                event.put("end_time", rs.getString("end_time"));
                event.put("location", rs.getString("location"));
                events.add(event);
            }
        }
        return events;
    }

    // Fetch all jobs for the organizer
    public static List<Map<String, String>> getOrganizerJobs(int userId) throws SQLException {
        List<Map<String, String>> jobs = new ArrayList<>();
        String sql = "SELECT job_title, event_title, job_location, employment_type, application_deadline, " +
                "min_salary, max_salary, currency, job_description, recruiter_name, recruiter_email, created_at " +
                "FROM jobs WHERE user_id = ?";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, SessionManager.getInstance().getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, String> job = new HashMap<>();
                job.put("job_title", rs.getString("job_title"));
                job.put("event_title", rs.getString("event_title"));
                job.put("job_location", rs.getString("job_location"));
                job.put("employment_type", rs.getString("employment_type"));
                job.put("application_deadline", rs.getString("application_deadline"));
                job.put("min_salary", rs.getString("min_salary"));
                job.put("max_salary", rs.getString("max_salary"));
                job.put("currency", rs.getString("currency"));
                job.put("job_description", rs.getString("job_description"));
                job.put("recruiter_name", rs.getString("recruiter_name"));
                job.put("recruiter_email", rs.getString("recruiter_email"));
                job.put("created_at", rs.getString("created_at"));
                jobs.add(job);
            }
        }

        return jobs;
    }
}