package com.example.projetjavafx.root.social;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class SocialController implements Initializable {

    @FXML private VBox postsContainer;
    @FXML private VBox createPostForm;
    @FXML private TextArea postContent;
    @FXML private ImageView userProfilePic;
    @FXML private ImageView selectedImage;
    @FXML private Label userName;
    @FXML private TextField searchField;

    @FXML private File selectedImageFile;
    @FXML private PostDAO postDAO;
    @FXML private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = AivenMySQLManager.getConnection();
            postDAO = new PostDAO(connection);
            loadUserProfile();
            loadAllPosts();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUserProfile() {
        try {
            URL imageUrl = getClass().getResource("/com/example/projetjavafx/social/img/user.png");
            if (imageUrl != null) {
                userProfilePic.setImage(new Image(imageUrl.toString()));
            }
            userName.setText("Current User"); // Replace with actual user data
        } catch (Exception e) {
            System.err.println("Error loading user profile: " + e.getMessage());
        }
    }

    @FXML
    private void openPostCreationModal() {
        createPostForm.setVisible(true);
        createPostForm.setManaged(true);
    }

    @FXML
    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            selectedImage.setImage(new Image(file.toURI().toString()));
            selectedImage.setVisible(true);
            selectedImage.setManaged(true);
        }
    }

    @FXML
    private void cancelPost() {
        clearPostForm();
    }

    @FXML
    private void publishPost() {
        String content = postContent.getText();
        String imagePath = null;
        if (selectedImageFile != null) {
            String uniqueFileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
            String targetDir = "images/";
            File targetFile = new File(targetDir + uniqueFileName);

            try {
                new File(targetDir).mkdirs();
                Files.copy(selectedImageFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = targetFile.getAbsolutePath();
            } catch (IOException e) {
                showAlert("Error", "Failed to save image: " + e.getMessage());
                return;
            }
        }
    }

    private void clearPostForm() {
        postContent.clear();
        selectedImage.setImage(null);
        selectedImage.setVisible(false);
        selectedImage.setManaged(false);
        selectedImageFile = null;
        createPostForm.setVisible(false);
        createPostForm.setManaged(false);
    }

    public void addPostToFeed(Post post) {
        try {
            // Save to database first
            postDAO.savePost(post);

            // Then add to UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/social/PostItem.fxml"));
            Node postNode = loader.load();

            PostItemController controller = loader.getController();
            controller.setData(post);

            // Add to the beginning of the feed
            postsContainer.getChildren().add(0, postNode);

        } catch (Exception e) {
            System.err.println("Error adding post to feed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPosts() {
        try {
            postsContainer.getChildren().clear();
            List<Post> posts = postDAO.getAllPosts();
            for (Post post : posts) {
                addPostToFeed(post);
            }
        } catch (SQLException e) {
            System.err.println("Error loading posts: " + e.getMessage());
        }
    }

   /* private void searchPosts(String searchText) throws SQLException {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadPosts();
            return;
        }

        postsContainer.getChildren().clear();
        List<Post> searchResults = postDAO.searchPosts(searchText);
        for (Post post : searchResults) {
            addPostToFeed(post);
        }
    }*/

    @FXML
    private void refreshFeed() {
        loadPosts();
    }

    public void deletePost(int postId) {
        postDAO.deletePost(postId);
        loadPosts(); // Refresh the feed after deletion
    }

    public void updatePost(Post post) {
        try {
            postDAO.updatePost(post);
            loadPosts(); // Refresh the feed after update
        } catch (SQLException e) {
            System.err.println("Error updating post: " + e.getMessage());
        }
    }

    public void handleInteraction(Post post, String interactionType) {
        try {
            // Update the interaction in the database (like, comment, or share)
            switch (interactionType) {
                case "LIKE":
                   // postDAO.addLike(post.getPostId(), getCurrentUserId());
                    break;
                case "COMMENT":
                    // Handle comment
                    break;
                case "SHARE":
                    // Handle share
                    break;
            }

            // Update popularity score
            postDAO.updatePopularityScore(post.getPostId());

            // Refresh the feed to show updated order
            loadAllPosts();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update post interaction: " + e.getMessage());
        }
    }

    private void loadAllPosts() {
        try {
            postsContainer.getChildren().clear();
            List<Post> posts = postDAO.getAllPosts();

            for (Post post : posts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/social/PostItem.fxml"));
                Node postNode = loader.load();
                PostItemController controller = loader.getController();
                controller.setData(post);
                postsContainer.getChildren().add(postNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load posts: " + e.getMessage());
        }
    }
   /* private void loadAllPosts() {
        try {
            // Clear existing posts
            postsContainer.getChildren().clear();

            // Get all posts from database
            List<Post> posts = postDAO.getAllPosts();

            // Add each post to the feed
            for (Post post : posts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/social/PostItem.fxml"));
                Node postNode = loader.load();

                PostItemController controller = loader.getController();
                controller.setData(post);

                postsContainer.getChildren().add(postNode);
            }
        } catch (Exception e) {
            System.err.println("Error loading posts: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    @FXML
    private void searchPosts(String searchText) {
        try {
            List<Post> searchResults = postDAO.searchPosts(searchText);
            postsContainer.getChildren().clear();

            for (Post post : searchResults) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetjavafx/social/PostItem.fxml"));
                Node postNode = loader.load();
                PostItemController controller = loader.getController();
                controller.setData(post);
                postsContainer.getChildren().add(postNode);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to search posts: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}