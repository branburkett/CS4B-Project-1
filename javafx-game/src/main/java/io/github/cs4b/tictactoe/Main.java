package io.github.cs4b.tictactoe;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        TicTacToeApp app = new TicTacToeApp();
        app.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}