package com.example.projetjavafx.root;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
import com.example.projetjavafx.root.mesg.client.ChatApplication;
import com.example.projetjavafx.root.mesg.server.ChatServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ApplicationRoot extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationRoot.class.getResource("/com/example/projetjavafx/root/root-view.fxml"));
        Parent root = fxmlLoader.load();
        double x = root.prefWidth(-1);
        double y = root.prefHeight(-1);
        Scene scene = new Scene(root,x,y);
        stage.setTitle("home!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

        // Start the server first
        new Thread(() -> {
            try {
                System.out.println("Starting chat server...");
                ChatServer.main(args);
            } catch (Exception e) {
                System.err.println("Error starting server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // Wait a bit for the server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Then start the client application
        System.out.println("Starting chat client application...");
        ChatApplication.main(args);
    }

}
