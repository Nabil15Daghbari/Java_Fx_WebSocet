package com.example.projetjavafx.root;

import com.example.projetjavafx.root.DbConnection.AivenMySQLManager;
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
    }
}
