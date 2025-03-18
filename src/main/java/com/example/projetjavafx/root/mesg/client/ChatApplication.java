package com.example.projetjavafx.root.mesg.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

public class ChatApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatView.fxml"));
        Parent root = loader.load();

        ChatController controller = loader.getController();

        // Initialize with current user (in a real app, this would come from login)
        String currentUsername = "currentUser"; // Replace with actual logged-in username

        // Make sure server is running before initializing client
        Thread serverThread = new Thread(() -> {
            boolean serverStarted = ServerManager.ensureServerRunning();
            System.out.println("Server startup result: " + (serverStarted ? "SUCCESS" : "FAILED"));

            // Initialize client after server is started
            Platform.runLater(() -> {
                try {
                    controller.initializeClient(currentUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // No need to wait for server thread to complete or call initializeClient here

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/chat-style.css").toExternalForm());

        primaryStage.setTitle("Real-time Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}