package com.example.projetjavafx.root.mesg.db;


import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.mesg.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MessageDB {

    public void saveMessage(Message message) {
        String query = "INSERT INTO Messages (sender_id, recipient_id, content, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getRecipientId());
            stmt.setString(3, message.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(message.getTimestamp()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getConversation(int user1Id, int user2Id) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Messages WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) ORDER BY timestamp ASC";

        try (Connection conn = AivenMySQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.setInt(3, user2Id);
            stmt.setInt(4, user1Id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setType("MESSAGE");
                message.setSenderId(rs.getInt("sender_id"));
                message.setRecipientId(rs.getInt("recipient_id"));
                message.setContent(rs.getString("content"));
                message.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());

                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}

