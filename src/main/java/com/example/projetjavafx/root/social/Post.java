package com.example.projetjavafx.root.social;

import java.time.LocalDateTime;

public class Post {

    private int postId;
    private int userId;
    private String content;
    private String imagePath;
    private String timestamp;
    private Integer eventId;
    private boolean isDeleted;
    private String createdAt;
    private String updatedAt;
    private int scorePopularite;
    private int groupId; // Add this field if it doesn't exist


    public Post(int userId, String content, Integer eventId, String imagePath) {
        this.userId = userId;
        this.content = content;
        this.imagePath = imagePath;
        this.eventId = eventId;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now().toString();
        this.updatedAt = this.createdAt;
        this.scorePopularite = 0;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getScorePopularite() { return scorePopularite; }
    public void setScorePopularite(int scorePopularite) { this.scorePopularite = scorePopularite; }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }
}