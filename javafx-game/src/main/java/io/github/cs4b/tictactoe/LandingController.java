package io.github.cs4b.tictactoe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LandingController {
    @FXML
    private Button singlePlayerButton;

    @FXML
    private void startSinglePlayerGame() {
        TicTacToeApp.showBoardScreen();
    }

    @FXML
    private void startLocalGame() {
        TicTacToeApp.showLocalBoardScreen();
    }

    @FXML
    private void startNetworkGame() {
        TicTacToeApp.showNetworkBoardScreen();
    }
}