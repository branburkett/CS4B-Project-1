package io.github.cs4b.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicTacToeApp extends Application {
    private static Stage primaryStage;
    
    public static final double WINDOW_WIDTH = (double)1170 * 0.31;
    public static final double WINDOW_HEIGHT = (double)2532 * 0.28;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLandingScreen();
    }

    public static void showLandingScreen() {
        try {
            Parent root = FXMLLoader.load(TicTacToeApp.class.getResource("/landing.fxml"));
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Add the CSS file to the scene
            scene.getStylesheets().add(TicTacToeApp.class.getResource("/landing.css").toExternalForm());
            System.out.println("landing.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Tic Tac Toe - Landing");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showBoardScreen() {
        try {
            Parent root = FXMLLoader.load(TicTacToeApp.class.getResource("/board2.fxml"));
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Add the CSS file to the scene
            scene.getStylesheets().add(TicTacToeApp.class.getResource("/board.css").toExternalForm());
            System.out.println("board.css");

            primaryStage.setScene(scene);
            primaryStage.setTitle("Tic Tac Toe - Game");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
