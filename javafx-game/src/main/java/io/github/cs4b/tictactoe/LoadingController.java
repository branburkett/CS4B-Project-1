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

    // Sets up a connection to the server, then holds the client
    // in the loading screen until another player joins
    @FXML
    public void initialize() {
        try {
            // Setup initial connection to server and let server know a connection has been made
            Socket socket = new Socket("localhost", 12345);
            NetworkManager manager = new NetworkManager(socket);
            manager.sendMessage(new Message.PlayerConnected("Player Connected"));
            TicTacToeApp.setNetworkManager(manager); // Store for BoardController to use

            // Initialize a seperate thread that will listen for messages
            Task<Void> listenerTask = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        while (true) {
                            Message msg = manager.receiveMessage();

                            // Go to game
                            if (msg.type == Message.MessageType.GAME_START) {

                                // Switch scenes on the JavaFX Thread
                                Platform.runLater(() -> {
                                    try {
                                        TicTacToeApp.showNetworkBoardScreen();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                                break;
                            }

                            // Store symbol assigned by server for the BoardController to use
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

            // Start running the seperate thread we initialized
            new Thread(listenerTask).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles any disconnections that need to be made and returns to the landing screen
    @FXML
    private void backToMenu() {
        NetworkManager manager = TicTacToeApp.network;
        
        // If the NetworkManager has already been assigned, remove it 
        // and let the server know a disconnect has occurred
        if (manager != null) {
            try {
                manager.sendMessage(new Message.PlayerDC());
                TicTacToeApp.network.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TicTacToeApp.showLandingScreen();
    }
}