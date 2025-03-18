package com.example.projetjavafx.root.social;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class PostItemController {
    @FXML private ImageView userAvatar;
    @FXML private Label userName;
    @FXML private Label postContent;
    @FXML private ImageView postImage;
    @FXML private Button likeButton;
    @FXML private Button commentButton;
    @FXML private Button shareButton;
    @FXML private Label likeCount;
    @FXML private Label commentCount;
    @FXML private Label shareCount;

    private Post post;
    private PostDAO postDAO;
    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = AivenMySQLManager.getConnection();
            postDAO = new PostDAO(connection);

            likeButton.setOnAction(e -> handleLike());
            commentButton.setOnAction(e -> handleComment());
            shareButton.setOnAction(e -> handleShare());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to initialize database connection");
        }
    }

    private void showAlert(Alert.AlertType alertType, String error, String failedToInitializeDatabaseConnection) {
    }

    public void setData(Post post) {
        // Set user avatar (default image for now)
        userAvatar.setImage(new Image(getClass().getResource("/com/example/projetjavafx/social/img/user.png").toString()));

        // Set username (you can get this from your user management system)
        userName.setText("User " + post.getUserId());

        // Set post content
        postContent.setText(post.getContent());

        // Set post image if exists
        if (post.getImagePath() != null) {
            File imageFile = new File(post.getImagePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                postImage.setImage(image);
                postImage.setVisible(true);
            } else {
                System.err.println("Error loading image. File not found: " + post.getImagePath());
                postImage.setVisible(false);
            }
        } else {
            postImage.setVisible(false);
        }
    }
    @FXML
    private void handleLike() {
        try {
           // postDAO.toggleLike(post.getPostId(), getCurrentUserId());
            updateLikeButton();
            updateCounts();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process like: " + e.getMessage());
        }
    }

    @FXML
    private void handleComment() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText(null);
        dialog.setContentText("Write your comment:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(comment -> {
            try {
              //  postDAO.addComment(post.getPostId(), getCurrentUserId(), comment);
                updateCounts();
                // Optionally show success message
                showAlert(Alert.AlertType.ERROR, "Success", "Comment added successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add comment: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleShare() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Share Post");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Do you want to share this post?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
               // postDAO.sharePost(post.getPostId(), getCurrentUserId());
                updateCounts();
                showAlert(Alert.AlertType.ERROR, "Success", "Post shared successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to share post: " + e.getMessage());
            }
        }
    }

    private void updateCounts() throws SQLException {
        likeCount.setText(String.valueOf(postDAO.getLikeCount(post.getPostId())));
        commentCount.setText(String.valueOf(postDAO.getCommentCount(post.getPostId())));
        shareCount.setText(String.valueOf(postDAO.getShareCount(post.getPostId())));
    }

    private void updateLikeButton() {
        // Update like button appearance based on whether the current user has liked the post
      //  try {
          //  boolean hasLiked = postDAO.hasUserLikedPost(getCurrentUserId(), post.getPostId());
          //  likeButton.setStyle(hasLiked ? "-fx-background-color: #2196F3;" : "-fx-background-color: transparent;");
      //  } catch (SQLException e) {
        //    e.printStackTrace();
        //}
    }
    @FXML private Button deleteButton;
    @FXML
    public void handleDelete() {
        if (post == null) {
            showAlert("Error", "No post selected");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Post");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to delete this post?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            postDAO.deletePost(post.getPostId());

            // Remove post from view
            Node postNode = deleteButton.getParent();
            while (!(postNode.getParent() instanceof VBox)) {
                postNode = postNode.getParent();
            }
            ((VBox) postNode.getParent()).getChildren().remove(postNode);
        }
    }

    public void setPost(Post post) {
        this.post = post;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


