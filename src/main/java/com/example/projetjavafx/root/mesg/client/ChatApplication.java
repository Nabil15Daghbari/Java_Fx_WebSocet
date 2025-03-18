package com.example.projetjavafx.root.mesg.client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class ChatApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatView.fxml"));
        Parent root = loader.load();

        ChatController controller = loader.getController();

        // Initialize with current user (in a real app, this would come from login)
        String currentUsername = "currentUser"; // Replace with actual logged-in username

        // Vérifier si le serveur est en cours d'exécution
        try {
            Socket socket = new Socket("localhost", 8887);
            socket.close();
            System.out.println("Chat server is running");
        } catch (IOException e) {
            System.err.println("Chat server is not running. Starting server...");
            // Vous pourriez démarrer le serveur ici si nécessaire
            // new Thread(() -> ChatServer.main(new String[]{})).start();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Not Running");
            alert.setHeaderText(null);
            alert.setContentText("The chat server is not running. Please start the server before using the chat application.");
            alert.showAndWait();
        }

        controller.initializeClient(currentUsername);

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

