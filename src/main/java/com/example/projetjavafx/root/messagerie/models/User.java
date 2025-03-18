package com.example.projetjavafx.root.messagerie.models;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt; // Attribut ajouté pour la date de création

    // Constructeur
    public User(int userId, String username, String email, String password, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.parse(createdAt);  // Conversion de String à LocalDateTime
    }

    // Getters et Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }  // Getter pour createdAt
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } // Setter pour createdAt
}
