package io.github.cs4b.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.net.URL;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Only declare the root and scene once
        URL fxmlUrl = getClass().getResource("/demo.fxml");
        if (fxmlUrl == null) {
            System.err.println("FXML file not found!");
            System.exit(0);
        }
        else {
            System.err.println("FXML file found!");
        }
 

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/demo.fxml"));
        Parent root = loader.load();  // Only declare once
        Scene scene = new Scene(root); // Only declare once

        primaryStage.setTitle("JavaFX Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
