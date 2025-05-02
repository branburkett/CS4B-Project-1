package tictactoe;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

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

    private char[][] board = new char[3][3]; // Board state
    private boolean gameActive = true;
    private int xWins = 0, oWins = 0, draws = 0;

    private NetworkManager networkManager;
    private char mySymbol;
    private boolean myTurn;

    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
        new Thread(this::listenToServer).start();
    }
    public void setSymbol(char symbol) {
        this.mySymbol = symbol;
    }
    public void setInitStatus(char symbol, boolean isYourTurn) {
        this.mySymbol = symbol;
        this.myTurn = isYourTurn;
    }

    @FXML
    public void initialize() {
        buttons = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};
        resetBoard();

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnAction(e -> handlePlayerMove(index));
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
                        myTurn = msg.isYourTurn;
                        break;
                    case TURN_CHANGE:
                        myTurn = !myTurn;
                    case GAME_OVER:
                        System.out.println(msg.payload);
                        break;
                    case INVALID_MOVE:
                        //updateStatus("Invalid move! Try again.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    // To update UI
    private void updateCell(int row, int col, String symbol) {
        board[row][col] = symbol.charAt(0);
        int index = row * 3 + col;
        buttons[index].getStyleClass().add(symbol.equals("X") ? "x" : "o");
        buttons[index].setText(symbol);

        if (checkWin(symbol.charAt(0))) {
            if (symbol.equals("X")) xWins++; else oWins++;
            updateScores();
            gameActive = false;
        } else if (isBoardFull()) {
            draws++;
            updateScores();
            gameActive = false;
        }
    }
        */

    @FXML
    private void goToMainMenu() {
        TicTacToeApp.showLandingScreen();
    }

    /*
    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)) return true;
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
               (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isBoardFull() {
        for (Button button : buttons) {
            if (button.getText().isEmpty()) return false;
        }
        return true;
    }
        */

    private void updateScores() {
        playerXScore.setText(String.valueOf(xWins));
        drawScore.setText(String.valueOf(draws));
        playerOScore.setText(String.valueOf(oWins));
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++) 
                board[i][j] = ' ';
        
        for (Button button : buttons) button.setText("");
        gameActive = true;
        myTurn = true; // Reset to the starting turn
    }
}
