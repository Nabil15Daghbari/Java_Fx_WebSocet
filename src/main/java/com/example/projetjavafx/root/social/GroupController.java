package com.example.projetjavafx.root.social;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import java.sql.SQLException;
import java.util.List;

public class GroupController {

    @FXML private ListView<Post> groupFeedListView;
    @FXML private TextArea newPostContent;
    @FXML private Button postButton;

    private PostDAO postDAO;
    private int currentGroupId;

    public void initialize() {
        loadGroupPosts();
        postButton.setOnAction(e -> {
            try {
                handleNewPost();
            } catch (SQLException ex) {
                showAlert("Database Error", "Failed to create new post: " + ex.getMessage());
            }
        });
    }

    private void loadGroupPosts() {
        try {
            List<Post> groupPosts = postDAO.getPostsByGroupId(currentGroupId);
            groupFeedListView.getItems().setAll(groupPosts);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load group posts: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleNewPost() throws SQLException {
        String content = newPostContent.getText().trim();
        if (!content.isEmpty()) {
            Post newPost = new Post(1, content, null, null); // Remplacez 1 par l'ID utilisateur actuel
            newPost.setGroupId(currentGroupId);
            postDAO.savePost(newPost);
            loadGroupPosts();
            newPostContent.clear();
        }
    }
}
