package com.example.projetjavafx.root.messagerie.models;

public class Notifications {
    private int notificationId;
    private int userId;
    private int messageId;
    private String content;
    private boolean isRead;
    private String createdAt;

    // Constructeur
    public Notifications(int notificationId, int userId, int messageId, String content, boolean isRead, String createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.messageId = messageId;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", messageId=" + messageId +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}