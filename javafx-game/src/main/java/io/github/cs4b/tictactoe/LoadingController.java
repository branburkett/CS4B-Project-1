package tictactoe;

import java.io.IOException;
import java.net.Socket;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoadingController {
    
    @FXML
    Button backButton;

    @FXML
    Label loadingLabel;

    private char symbol;

    @FXML
public void initialize() {
    try {
        Socket socket = new Socket("localhost", 12345);
        NetworkManager manager = new NetworkManager(socket);
        manager.sendMessage(new Message.PlayerConnected("Player Connected"));
        TicTacToeApp.setNetworkManager(manager); // Store for BoardController to use

        Task<Void> listenerTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    while (true) {
                        Message msg = manager.receiveMessage();
                        if (msg.type == Message.MessageType.GAME_START) {

                            // Always switch scenes on the JavaFX thread
                            Platform.runLater(() -> {
                                try {
                                    TicTacToeApp.showNetworkBoardScreen();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            break;
                        }
                        if (msg.type == Message.MessageType.PLAYER_CONNECTED) {
                            symbol = msg.payload.charAt(0);
                            TicTacToeApp.setSymbol(symbol);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(listenerTask).start();

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @FXML
    private void backToMenu() {
        TicTacToeApp.showLandingScreen();
    }

    /*
    private void listenToServer() {
        try {
            while(true) {
                Message message = networkManager.receiveMessage();
                if (message.type == Message.MessageType.GAME_START) {
                    System.out.println("GAME_START received! Transitioning to board...");
                    break;
                }
                if (message.type == Message.MessageType.PLAYER_CONNECTED) {
                    System.out.println(message.payload);
                }
            }
            TicTacToeApp.showNetworkBoardScreen(networkManager);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        */
}