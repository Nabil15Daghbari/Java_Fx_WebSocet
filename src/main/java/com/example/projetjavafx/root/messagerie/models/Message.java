package com.example.projetjavafx.root.messagerie.models;

import java.time.LocalDateTime;
import java.util.Objects;


public class Message {
    private int messageId;
    private int senderId;
    private Integer receiverId; // Peut être null si le message est pour un groupe
    private Integer groupId; // Peut être null si le message est pour un utilisateur
    private String content;
    private LocalDateTime timestamp; // ✅ Utilisation de LocalDateTime
    private String senderUsername; // ✅ Ajouter un champ pour stocker le username du sender

    public Message() {
        // Constructeur sans argument
    }

    // Constructeur pour les messages entre utilisateurs
    public Message(int messageId, int senderId, Integer receiverId, String content,LocalDateTime timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = null; // Pas de groupe
        this.content = content;
        this.timestamp = timestamp;
    }

    // Constructeur pour les messages de groupe
    public Message(int messageId, int senderId, Integer groupId, String content, LocalDateTime timestamp, boolean isGroupMessage) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = null; // Pas de destinataire individuel
        this.groupId = groupId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    // Getters et Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }

    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        { this.timestamp = timestamp; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageId == message.messageId && senderId == message.senderId &&
                Objects.equals(receiverId, message.receiverId) &&
                Objects.equals(groupId, message.groupId) &&
                Objects.equals(content, message.content) &&
                Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, senderId, receiverId, groupId, content, timestamp);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", groupId=" + groupId +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }


}
