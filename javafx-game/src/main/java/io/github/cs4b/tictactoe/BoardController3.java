package tictactoe;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class BoardController3 {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gamePane;

    @FXML
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @FXML
    private Button newGame;

    @FXML
    private Button backButton; // Back button for main menu

    @FXML
    private Label playerXScore, drawScore, playerOScore;

    @FXML
    private Button[] buttons; // Store button references

    @FXML
    private Label[] labels;

    @FXML
    private Label buttonHover1, buttonHover2, buttonHover3, buttonHover4, buttonHover5, buttonHover6, buttonHover7, buttonHover8, buttonHover9;

    private char[][] board = new char[3][3]; // Board state
    private boolean gameActive = true;

    private NetworkManager networkManager;
    private char mySymbol;
    private boolean myTurn;

    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
        new Thread(this::listenToServer).start();
    }
    public void setInitStatus(char symbol, boolean isYourTurn) {
        this.mySymbol = symbol;
        this.myTurn = isYourTurn;
        Platform.runLater(() -> {                           // Initialize labels in here because the symbol is
            for (int i = 0; i < buttons.length; i++) {      // set before we setup the labels
                setupButtonHover(buttons[i], labels[i]);
            }
        });
    }

    @FXML
    public void initialize() {
        buttons = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};
        labels = new Label[]{buttonHover1, buttonHover2, buttonHover3, buttonHover4, buttonHover5, buttonHover6, buttonHover7, buttonHover8, buttonHover9};
        resetBoard();

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnAction(e -> handlePlayerMove(index));

            //setupButtonHover(buttons[i], labels[i]);
        }

        newGame.setOnAction(e -> resetBoard());
        backButton.setOnAction(e -> goToMainMenu());
    }

    private void handlePlayerMove(int index) {
        if (!gameActive || !buttons[index].getText().isEmpty() || !myTurn) return;
        if (!myTurn) return;

        int row = index / 3, col = index % 3;
        String movePayload = row + "," + col + "," + mySymbol;

        try {
            networkManager.sendMessage(new Message.MoveMade(movePayload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update board based on data from server
    private void updateCellFromServer(String payload) {
        // Extract row, column, and symbol from the payload
        String[] parts = payload.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        char symbol = parts[2].charAt(0);
    
        // Update the board array
        board[row][col] = symbol;
    
        // Get the index of the button corresponding to the cell (row, col)
        int index = row * 3 + col;
    
        // Update the button text to show the player's symbol
        buttons[index].setText(String.valueOf(symbol));
    
        // Add style based on the symbol (X or O)
        buttons[index].getStyleClass().add(symbol == 'X' ? "x" : "o");

    }
    

    private void listenToServer() {
        try {
            while (true) {
                Message msg = networkManager.receiveMessage();
                if (msg == null) {
                    break;
                }
    
                switch (msg.type) {
                    case MOVE_MADE:
                        updateCellFromServer(msg.payload); // Update board from server message
                        myTurn = !myTurn;
                        toggleLabels();
                        break;
                    case GAME_OVER:
                        System.out.println(msg.payload);
                        Platform.runLater(() -> updateScores(msg.payload));
                        myTurn = false; // Run on javafx UI thread
                        break;
                    case INVALID_MOVE:
                        break;
                    case GAME_START:
                        
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @FXML
    private void goToMainMenu() {
        TicTacToeApp.showLandingScreen();
    }

    private void updateScores(String payload) {
        String[] parts = payload.split(",");
        String type = parts[0];
        int wins = Integer.parseInt(parts[1]);
        if (type.equals("X")) {
            playerXScore.setText(String.valueOf(wins));
        }
        else if (type.equals("O")) {
            playerOScore.setText(String.valueOf(wins));
        }
        else {
            drawScore.setText(String.valueOf(wins));
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++) 
                board[i][j] = ' ';
        
        for (Button button : buttons) button.setText("");
        gameActive = true;
        //for (int i = 0; i < 9; i++) setupButtonHover(buttons[i], labels[i]);
    }
    private void setupButtonHover(Button button, Label label) {
        label.setOpacity(0.0);
        label.setMouseTransparent(true);
        label.setText(String.valueOf(mySymbol));
        label.setVisible(myTurn);
    
        FadeTransition fadeIn = new FadeTransition(Duration.millis(160), label);
        fadeIn.setToValue(0.5);
    
        FadeTransition fadeOut = new FadeTransition(Duration.millis(175), label);
        fadeOut.setToValue(0.0);

        button.setOnMouseEntered(event -> {
            if (myTurn == false || !button.getText().isEmpty()) return;
            fadeOut.stop();
            fadeIn.playFromStart();
        });
    
        button.setOnMouseExited(event -> {
            fadeIn.stop();
            fadeOut.playFromStart();
        });
    }
    private void toggleLabels() {
        for (int i = 0; i < 9; i++) {
            labels[i].setVisible(myTurn);
        }
    }
}
