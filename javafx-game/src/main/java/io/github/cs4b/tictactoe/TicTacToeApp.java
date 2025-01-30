package io.github.cs4b.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicTacToeApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLandingScreen();
    }

    public static void showLandingScreen() {
        try {
            Parent root = FXMLLoader.load(TicTacToeApp.class.getResource("/landing.fxml"));
            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.setTitle("Tic Tac Toe - Landing");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showBoardScreen() {
        try {
            Parent root = FXMLLoader.load(TicTacToeApp.class.getResource("/board.fxml"));
            primaryStage.setScene(new Scene(root, 400, 400));
            primaryStage.setTitle("Tic Tac Toe - Game");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}