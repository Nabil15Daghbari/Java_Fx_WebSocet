package com.example.projetjavafx.root.mesg.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatView.fxml"));
        Parent root = loader.load();

        ChatController controller = loader.getController();

        // Initialize with current user (in a real app, this would come from login)
        String currentUsername = "currentUser"; // Replace with actual logged-in username

        // Start the server in a separate thread before initializing the UI
        Thread serverThread = new Thread(() -> {
            boolean serverStarted = ServerManager.ensureServerRunning();
            System.out.println("Server startup result: " + (serverStarted ? "SUCCESS" : "FAILED"));
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait for server thread to complete
        try {
            serverThread.join(10000); // Wait up to 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now initialize the client
        controller.initializeClient(currentUsername);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/view/discussion.css").toExternalForm());

        primaryStage.setTitle("Real-time Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

