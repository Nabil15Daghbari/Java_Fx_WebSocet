package com.example.projetjavafx.root.mesg.client;

import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.projetjavafx.root.mesg.model.Message;
import com.example.projetjavafx.root.mesg.model.User;
import com.example.projetjavafx.root.mesg.repository.UserDB;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;



public class ChatController {

    @FXML private ListView<User> userListView;
    @FXML private TextField searchField;
    @FXML private VBox chatBox;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Label selectedUserLabel;
    @FXML private Label connectionStatusLabel;

    private ChatClient client;
    private int currentUserId;
    private int selectedUserId = -1;
    private UserDB userDB = new UserDB();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public void initialize() {
        // Set up UI components
        userListView.setItems(userList);
        userListView.setCellFactory(lv -> new UserListCell());

        // Initialize connection status
        connectionStatusLabel.setText("Disconnected");
        connectionStatusLabel.setTextFill(Color.RED);

        // Handle user selection
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUserId = newVal.getUserId();
                selectedUserLabel.setText("Chat with " + newVal.getUsername());
                chatBox.getChildren().clear(); // Clear previous chat
                // Here you would load previous messages from database
            }
        });

        // Set up search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchUsers(newVal);
        });

        // Set up message sending
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    public void initializeClient(String username) {
        try {
            // Make sure server is running
            ServerManager.ensureServerRunning();

            // Get current user ID
            currentUserId = userDB.getUserIdByUsername(username);

            if (currentUserId == -1) {
                // Si l'utilisateur n'existe pas, créons-en un pour les tests
                System.out.println("User not found, creating a test user: " + username);
                currentUserId = 1; // ID par défaut pour les tests
            }

            // Load all users
            loadUsers();

            // Connect to WebSocket server
            connectToServer();

        } catch (Exception e) {
            System.err.println("Error initializing client: " + e.getMessage());
            e.printStackTrace();
            showAlert("Initialization Error", "Failed to initialize chat client: " + e.getMessage());
        }
    }

    // Modify the connectToServer method to be more robust
    private void connectToServer() {
        try {
            System.out.println("Connecting to WebSocket server...");
            connectionStatusLabel.setText("Connecting...");
            connectionStatusLabel.setTextFill(Color.ORANGE);

            // Make sure server is running before attempting to connect
            boolean serverRunning = ServerManager.ensureServerRunning();
            if (!serverRunning) {
                System.err.println("Server is not running and could not be started");
                connectionStatusLabel.setText("Server Unavailable");
                connectionStatusLabel.setTextFill(Color.RED);
                showAlert("Connection Error", "Could not connect to chat server. Please try again later.");
                return;
            }

            // Wait a bit to ensure server is fully initialized
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Create and connect the client
            client = new ChatClient("ws://localhost:8887", this, currentUserId);

            // Set connection timeout
            client.setConnectionLostTimeout(0); // Disable timeout for testing

            // Connect with explicit try-catch
            try {
                client.connectBlocking(5, TimeUnit.SECONDS); // Wait up to 5 seconds for connection
                System.out.println("WebSocket connection established: " + client.isOpen());
            } catch (Exception e) {
                System.err.println("Error during WebSocket connection: " + e.getMessage());
                e.printStackTrace();
                connectionStatusLabel.setText("Connection Failed");
                connectionStatusLabel.setTextFill(Color.RED);
                showAlert("Connection Error", "Failed to connect to chat server: " + e.getMessage());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid WebSocket URI: " + e.getMessage());
            e.printStackTrace();
            connectionStatusLabel.setText("Connection Error");
            connectionStatusLabel.setTextFill(Color.RED);
            showAlert("Connection Error", "Invalid WebSocket server address");
        }
    }

    public void onConnectionEstablished() {
        connectionStatusLabel.setText("Connected");
        connectionStatusLabel.setTextFill(Color.GREEN);

        // Display system message
        Message systemMsg = new Message("SYSTEM", 0, currentUserId,
                "Connected to chat server. You can now send messages.");
        displayMessage(systemMsg);
    }

    public void onConnectionClosed(String reason) {
        connectionStatusLabel.setText("Disconnected");
        connectionStatusLabel.setTextFill(Color.RED);

        // Display system message
        Message systemMsg = new Message("SYSTEM", 0, currentUserId,
                "Disconnected from server: " + reason);
        displayMessage(systemMsg);
    }

    public void onConnectionError(String errorMessage) {
        connectionStatusLabel.setText("Connection Error");
        connectionStatusLabel.setTextFill(Color.RED);

        // Display system message
        Message systemMsg = new Message("SYSTEM", 0, currentUserId,
                "Connection error: " + errorMessage);
        displayMessage(systemMsg);
    }

    private void loadUsers() {
        // Fetch all users from the database
        List<User> users = userDB.getAllUsers();

        // For testing, if the database is empty, add some sample users
        if (users.isEmpty()) {
            // Add some sample users for testing
            users.add(new User(1, "user1"));
            users.add(new User(2, "user2"));
            users.add(new User(3, "user3"));
        }

        Platform.runLater(() -> {
            userList.clear();
            userList.addAll(users);
        });
    }

    private void searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            loadUsers();
            return;
        }

        // Filter users based on search term
        List<User> filteredUsers = userDB.searchUsers(searchTerm);

        // If no results from database or for testing
        if (filteredUsers.isEmpty()) {
            // Filter the existing list
            filteredUsers = new ArrayList<>();
            for (User user : userList) {
                if (user.getUsername().toLowerCase().contains(searchTerm.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }
        }

        List<User> finalFilteredUsers = filteredUsers;
        Platform.runLater(() -> {
            userList.clear();
            userList.addAll(finalFilteredUsers);
        });
    }

    private void sendMessage() {
        if (selectedUserId == -1) {
            showAlert("Error", "Please select a user to chat with");
            return;
        }

        String content = messageField.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        // Vérifier si le client est connecté
        if (client == null || !client.isConnected()) {
            System.err.println("WebSocket client is not connected. Attempting to reconnect...");

            // Display reconnecting message
            Message systemMsg = new Message("SYSTEM", 0, currentUserId,
                    "Not connected to server. Attempting to reconnect...");
            displayMessage(systemMsg);

            // Try to reconnect
            connectToServer();

            // Wait a bit and check if we're connected
            new Thread(() -> {
                try {
                    Thread.sleep(2000);

                    Platform.runLater(() -> {
                        if (client != null && client.isConnected()) {
                            // Now we're connected, try to send the message
                            sendMessageInternal(content);
                        } else {
                            // Still not connected
                            Message errorMsg = new Message("SYSTEM", 0, currentUserId,
                                    "Could not connect to server. Please try again later.");
                            displayMessage(errorMsg);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            return;
        }

        // We're connected, send the message
        sendMessageInternal(content);
    }

    private void sendMessageInternal(String content) {
        try {
            // Send message via WebSocket
            client.sendMessage(selectedUserId, content);

            // Also display the sent message in our own chat
            Message sentMsg = new Message("MESSAGE", currentUserId, selectedUserId, content);
            displayMessage(sentMsg);

            // Clear the input field
            messageField.clear();
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();

            // Display error message
            Message errorMsg = new Message("SYSTEM", 0, currentUserId,
                    "Failed to send message: " + e.getMessage());
            displayMessage(errorMsg);
        }
    }

    public void displayMessage(Message message) {
        // Only display messages that are relevant to the current chat
        if (message.getType().equals("SYSTEM") ||
                (message.getSenderId() == selectedUserId && message.getRecipientId() == currentUserId) ||
                (message.getSenderId() == currentUserId && message.getRecipientId() == selectedUserId)) {

            HBox messageContainer = new HBox(10);

            // Create message bubble
            TextFlow bubble = new TextFlow();
            Text text = new Text(message.getContent());
            bubble.getChildren().add(text);

            // Style based on sender
            if (message.getType().equals("SYSTEM")) {
                bubble.getStyleClass().add("system-message");
                messageContainer.setAlignment(Pos.CENTER);
            } else if (message.getSenderId() == currentUserId) {
                bubble.getStyleClass().add("sent-message");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                bubble.getStyleClass().add("received-message");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            }

            // Add timestamp
            Text timeText = new Text(message.getTimestamp().format(timeFormatter));
            timeText.getStyleClass().add("time-text");

            messageContainer.getChildren().addAll(bubble, timeText);

            Platform.runLater(() -> {
                chatBox.getChildren().add(messageContainer);
            });
        }
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    // Custom cell for user list
    private class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox container = new HBox(10);
                Label nameLabel = new Label(user.getUsername());

                // Create status indicator
                javafx.scene.shape.Circle statusIndicator = UserStatusCircle.createStatusCircle(5);

                container.getChildren().addAll(statusIndicator, nameLabel);
                setGraphic(container);
            }
        }
    }
}

