package tictactoe;

import java.io.IOException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoadingController {
    
    @FXML
    Button backButton;

    @FXML
    Label loadingLabel;

    private NetworkManager networkManager;

    @FXML
    public void initialize() {
        try {
            Socket socket = new Socket("localhost", 12345);
            networkManager = new NetworkManager(socket);
            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToMenu() {
        TicTacToeApp.showLandingScreen();
    }
}
