package com.example.projetjavafx.root.messagerie.models;

import java.time.LocalDateTime;

public class UserMessages {
    private int userId;
    private String username;
    private String lastMessage;
    private LocalDateTime sentAt;

    public UserMessages(int userId, String username, String lastMessage, LocalDateTime sentAt) {
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.sentAt = sentAt;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }
}
