package com.example.projetjavafx.root.messagerie.models;

public class UserGroup {
    private int groupId;
    private String name;
    private String description;
    private String profilePicture;
    private String rules;
    private String createdAt;

    public UserGroup(int groupId, String name, String description, String profilePicture, String rules, String createdAt) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.profilePicture = profilePicture;
        this.rules = rules;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
