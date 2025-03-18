package com.example.projetjavafx.root.social;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    private Connection connection;

    public PostDAO(Connection connection) {
        this.connection = connection;
    }

    public void savePost(Post post) throws SQLException {
        String sql = "INSERT INTO FeedPosts (user_id, content, event_id, is_deleted, created_at, updated_at, score_popularite, image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, post.getUserId());
            pstmt.setString(2, post.getContent());
            if (post.getEventId() != null) {
                pstmt.setInt(3, post.getEventId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setInt(4, post.isDeleted() ? 1 : 0);
            pstmt.setString(5, post.getCreatedAt());
            pstmt.setString(6, post.getUpdatedAt());
            pstmt.setInt(7, post.getScorePopularite());
            pstmt.setString(8, post.getImagePath());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                post.setPostId(rs.getInt(1));
            }
        }
    }
    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, " +
                "(SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.post_id) as like_count, " +
                "(SELECT COUNT(*) FROM Comments c WHERE c.post_id = p.post_id) as comment_count, " +
                "(SELECT COUNT(*) FROM Shares s WHERE s.post_id = p.post_id) as share_count " +
                "FROM FeedPosts p " +
                "WHERE p.is_deleted = 0 " +
                "ORDER BY (like_count + comment_count + share_count) DESC, p.created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getInt("event_id"),
                        rs.getString("image_path")
                );
                post.setPostId(rs.getInt("post_id"));
                post.setCreatedAt(rs.getString("created_at"));
                post.setUpdatedAt(rs.getString("updated_at"));

                // Calculate and set popularity score
                int likeCount = rs.getInt("like_count");
                int commentCount = rs.getInt("comment_count");
                int shareCount = rs.getInt("share_count");
                int popularityScore = likeCount + commentCount + shareCount;
                post.setScorePopularite(popularityScore);

                posts.add(post);
            }
        }
        return posts;
    }

    public void updatePopularityScore(int postId) throws SQLException {
        String sql = "UPDATE FeedPosts SET score_popularite = " +
                "(SELECT COUNT(*) FROM Likes WHERE post_id = ?) + " +
                "(SELECT COUNT(*) FROM Comments WHERE post_id = ?) + " +
                "(SELECT COUNT(*) FROM Shares WHERE post_id = ?) " +
                "WHERE post_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, postId);
            pstmt.setInt(3, postId);
            pstmt.setInt(4, postId);
            pstmt.executeUpdate();
        }
    }


    public void updatePost(Post post) throws SQLException {
        String sql = "UPDATE FeedPosts SET content = ?, updated_at = ? WHERE post_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, post.getContent());
            pstmt.setString(2, LocalDateTime.now().toString());
            pstmt.setInt(3, post.getPostId());
            pstmt.executeUpdate();
        }
    }
    public List<Post> searchPosts(String searchText) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM FeedPosts WHERE content LIKE ? AND is_deleted = 0 ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchText + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getInt("user_id"),
                            rs.getString("content"),
                            rs.getInt("event_id"),
                            rs.getString("image_path"));
                    post.setPostId(rs.getInt("post_id"));
                    post.setTimestamp(rs.getString("timestamp"));
                    post.setDeleted(rs.getBoolean("is_deleted"));
                    post.setCreatedAt(rs.getString("created_at"));
                    post.setUpdatedAt(rs.getString("updated_at"));
                    post.setScorePopularite(rs.getInt("score_popularite"));
                    posts.add(post);
                }
            }
        }
        return posts;
    }
    public void toggleLike(int postId, int userId) throws SQLException {
        // Check if user already liked the post
        String checkSql = "SELECT like_id FROM Likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, postId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Unlike: Remove existing like
                String deleteSql = "DELETE FROM Likes WHERE post_id = ? AND user_id = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, postId);
                    deleteStmt.setInt(2, userId);
                    deleteStmt.executeUpdate();
                }
            } else {
                // Like: Add new like
                String insertSql = "INSERT INTO Likes (post_id, user_id) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, postId);
                    insertStmt.setInt(2, userId);
                    insertStmt.executeUpdate();
                }
            }
        }
        updatePopularityScore(postId);
    }

    public void addComment(int postId, int userId, String content) throws SQLException {
        String sql = "INSERT INTO Comments (post_id, user_id, content) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        }
        updatePopularityScore(postId);
    }

    public void sharePost(int postId, int userId) throws SQLException {
        String sql = "INSERT INTO Shares (post_id, user_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
        updatePopularityScore(postId);
    }

    public int getLikeCount(int postId) throws SQLException {
        return getCount("Likes", postId);
    }

    public int getCommentCount(int postId) throws SQLException {
        return getCount("Comments", postId);
    }

    public int getShareCount(int postId) throws SQLException {
        return getCount("Shares", postId);
    }

    private int getCount(String table, int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE post_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Post> getPostsByGroupId(int groupId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM FeedPosts WHERE group_id = ? AND is_deleted = 0 ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getInt("user_id"),
                            rs.getString("content"),
                            rs.getInt("event_id"),
                            rs.getString("image_path")
                    );
                    post.setPostId(rs.getInt("post_id"));
                    post.setCreatedAt(rs.getString("created_at"));
                    post.setUpdatedAt(rs.getString("updated_at"));
                    post.setScorePopularite(rs.getInt("score_popularite"));
                    posts.add(post);
                }
            }
        }
        return posts;
    }

    public void deletePost(int postId) {
    }
}
