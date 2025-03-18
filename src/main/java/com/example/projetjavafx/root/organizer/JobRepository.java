package com.example.projetjavafx.root.organizer;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.auth.SessionManager;
import com.mysql.cj.Session;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JobRepository {
    public static void createJob(String jobTitle , String eventTitle ,String jobLocation,String employmentType,String applicationDeadline,String minSalary,String maxSalary,String currency,String jobDescription , String recruiterName , String recruiterEmail) throws SQLException {


        Connection conn = AivenMySQLManager.getConnection();
        String sql = "INSERT INTO jobs (job_title, event_title, job_location, employment_type, application_deadline, min_salary, max_salary, currency, job_description, recruiter_name, recruiter_email, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // Set parameters

        pstmt.setString(1, jobTitle);
        pstmt.setString(2,eventTitle );
        pstmt.setString(3, jobLocation );
        pstmt.setString(4, employmentType);
        pstmt.setString(5, applicationDeadline );
        pstmt.setDouble(6, Double.parseDouble(minSalary));
        pstmt.setDouble(7, Double.parseDouble(maxSalary));
        pstmt.setString(8, currency );
        pstmt.setString(9, jobDescription);
        pstmt.setString(10, recruiterName);
        pstmt.setString(11, recruiterEmail);
        pstmt.setInt(12, SessionManager.getInstance().getCurrentUserId()); // Replace with the logged-in user's ID (e.g., from session)

        // Execute the query
        pstmt.executeUpdate();


    }
}