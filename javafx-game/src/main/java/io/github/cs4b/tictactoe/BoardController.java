package io.github.cs4b.tictactoe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class BoardController {
    @FXML
    private GridPane boardGrid;
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;
    @FXML
    private Label currentTurnLabel;

    private boolean isXTurn = true;  // true for Player 1 (X), false for Player 2 (O)
    private int moveCount = 0;
    private Button[][] buttons = new Button[3][3];  // 2D array to hold buttons for the game board

    @FXML
    public void initialize() {
        // Initialize the game board dynamically
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setMinSize(100, 100);
                button.setStyle("-fx-font-size: 24px;");
                int row = i, col = j;

                button.setOnAction(e -> handleMove(button, row, col));
                buttons[i][j] = button;
                boardGrid.add(button, j, i);
            }
        }
    }

    // Handle a player's move
    private void handleMove(Button button, int row, int col) {
        if (button.getText().isEmpty()) {
            button.setText(isXTurn ? "X" : "O");
            moveCount++;
            if (checkWin(row, col)) {
                currentTurnLabel.setText("Player " + (isXTurn ? "1" : "2") + " Wins!");
                disableBoard();  // Disable the board once a player wins
            } else if (moveCount == 9) {
                currentTurnLabel.setText("Tie!");
            } else {
                isXTurn = !isXTurn;  // Switch turns
                updateTurnLabel();
            }
        }
    }

    // Update the current turn label
    private void updateTurnLabel() {
        if (isXTurn) {
            currentTurnLabel.setText("Player 1's turn!");
        } else {
            currentTurnLabel.setText("Player 2's turn!");
        }
    }

    // Check if the current player has won
    private boolean checkWin(int row, int col) {
        String currentPlayer = isXTurn ? "X" : "O";

        // Check row
        if (buttons[row][0].getText().equals(currentPlayer) &&
            buttons[row][1].getText().equals(currentPlayer) &&
            buttons[row][2].getText().equals(currentPlayer)) {
            return true;
        }

        // Check column
        if (buttons[0][col].getText().equals(currentPlayer) &&
            buttons[1][col].getText().equals(currentPlayer) &&
            buttons[2][col].getText().equals(currentPlayer)) {
            return true;
        }

        // Check diagonals
        if (row == col) {
            if (buttons[0][0].getText().equals(currentPlayer) &&
                buttons[1][1].getText().equals(currentPlayer) &&
                buttons[2][2].getText().equals(currentPlayer)) {
                return true;
            }
        }
        if (row + col == 2) {
            if (buttons[0][2].getText().equals(currentPlayer) &&
                buttons[1][1].getText().equals(currentPlayer) &&
                buttons[2][0].getText().equals(currentPlayer)) {
                return true;
            }
        }

        return false;
    }

    // Disable the board once the game is over
    private void disableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(true);  // Disable all buttons after the game ends
            }
        }
    }

    // Method to switch back to the landing screen and reset the board
    @FXML
    private void goBackToMenu() {
        TicTacToeApp.showLandingScreen();
        resetBoard();  // Reset the board when going back to the menu
    }

    // Method to reset the game board
    private void resetBoard() {
        // Reset all buttons on the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");  // Clear text
                buttons[i][j].setDisable(false);  // Enable buttons
            }
        }
        // Reset game state
        moveCount = 0;
        isXTurn = true;
        currentTurnLabel.setText("Player 1's turn!");
    }
}