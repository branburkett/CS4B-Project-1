package io.github.cs4b.tictactoe;

import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class BoardController2 {

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
    private StackPane popupPane, turnSelectionPopup; // Popup container
    
    @FXML
    private Label popupMessage, turnSelectionMessage; // Popup text

    @FXML
    private Button playerFirstButton, aiFirstButton;

    private char[][] board = new char[3][3]; // Board state
    private boolean gameActive = true;
    private boolean playerGoesFirst = true;
    private int xWins = 0, oWins = 0, draws = 0;

    @FXML
    private Label[] labels;
    
    @FXML
    private Label buttonHover1, buttonHover2, buttonHover3, buttonHover4, buttonHover5, buttonHover6, buttonHover7, buttonHover8, buttonHover9;

    @FXML
    public void initialize() {
        buttons = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};
        labels = new Label[]{buttonHover1, buttonHover2, buttonHover3, buttonHover4, buttonHover5, buttonHover6, buttonHover7, buttonHover8, buttonHover9};
        resetBoard();
        
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnAction(e -> handlePlayerMove(index));

            setupButtonHover(buttons[i], labels[i]);
        }
        
        newGame.setOnAction(e -> resetBoard());
        backButton.setOnAction(e -> goToMainMenu());

        playerFirstButton.setOnAction(e -> {
            playerGoesFirst = true;
            startGame();
        });

        aiFirstButton.setOnAction(e -> {
            playerGoesFirst = false;
            startGame();
        });

        showTurnSelectionPopup(); // Show popup on entry
    }

    @FXML
    private void goToMainMenu() {
        TicTacToeApp.showLandingScreen();
    }

    private void handlePlayerMove(int index) {
        if (!gameActive || !buttons[index].getText().isEmpty()) return;
        
        int row = index / 3, col = index % 3;
        board[row][col] = 'X';

        buttons[index].getStyleClass().add("x");
        buttons[index].setText("X");

        labels[index].setVisible(false);

        if (checkWin('X')) {
            xWins++;
            updateScores();
            gameActive = false;
            showPopup("Player X Wins!");
            return;
        }

        if (isBoardFull()) {
            draws++;
            updateScores();
            gameActive = false;
            showPopup("It's a Tie!");
            return;
        }

        aiMove();
    }

    private void aiMove() {
        if (!gameActive) return;

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
    
        // Loop through all possible moves
        for (int i = 0; i < 9; i++) {
            int row = i / 3, col = i % 3;
    
            if (board[row][col] == ' ') { // If the spot is empty
                board[row][col] = 'O'; // AI makes a temporary move
                int score = minimax(board, 0, false); // Run minimax
                board[row][col] = ' '; // Undo move
    
                if (score > bestScore) { // Maximize AI's move
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
    
        // Make the best move found
        if (bestMove != -1) {
            int row = bestMove / 3, col = bestMove % 3;
            board[row][col] = 'O';
            buttons[bestMove].getStyleClass().add("o");
            buttons[bestMove].setText("O");
            labels[bestMove].setVisible(false);
    
            // Check if AI wins
            if (checkWin('O')) {
                oWins++;
                updateScores();
                gameActive = false;
                showPopup("Player O Wins!");
            } else if (isBoardFull()) {
                draws++;
                updateScores();
                gameActive = false;
                showPopup("It's a Tie!");
            }
        }
    }
    
    
    private int minimax(char[][] board, int depth, boolean isMaximizing) {
    
        if (checkWin('O')) {
            return 10 - depth;  // AI wins faster, so subtract depth
        }
        if (checkWin('X')) {
            return depth - 10;  // Player wins, minimize loss
        }
        if (isBoardFull()) {
            return 0;
        }
    
        if (isMaximizing) { // AI's turn
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                int row = i / 3, col = i % 3;
                if (board[row][col] == ' ') {
                    board[row][col] = 'O'; // AI makes a move
                    int eval = minimax(board, depth + 1, false);
                    board[row][col] = ' '; // Undo move
                    maxEval = Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else { // Human's turn
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                int row = i / 3, col = i % 3;
                if (board[row][col] == ' ') {
                    board[row][col] = 'X'; // Player makes a move
                    int eval = minimax(board, depth + 1, true);
                    board[row][col] = ' '; // Undo move
                    minEval = Math.min(minEval, eval);
                }
            }
            return minEval;
        }
    }
    
    // Helper function to print the board
    private void printBoard(char[][] board) {
        for (int i = 0; i < 3; i++) {
            System.out.println(board[i][0] + " " + board[i][1] + " " + board[i][2]);
        }
        System.out.println();
    }
    
    

    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)) return true;
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
               (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false; // Empty spot found, board is not full
                }
            }
        }
        return true; // No empty spots, board is full
    }
/* 
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
        popupPane.setVisible(false);

        for (int i = 0; i < 9; i++) {
            setupButtonHover(buttons[i], labels[i]);
        }

        showTurnSelectionPopup(); // Ask who goes first
    }

    private void setupButtonHover(Button button, Label label) {
        label.setOpacity(0.0);
        label.setMouseTransparent(true);
        label.setText("x");
        label.setVisible(true);
    
        FadeTransition fadeIn = new FadeTransition(Duration.millis(160), label);
        fadeIn.setToValue(0.5);
    
        FadeTransition fadeOut = new FadeTransition(Duration.millis(175), label);
        fadeOut.setToValue(0.0);

        button.setOnMouseEntered(event -> {
            fadeOut.stop();
            fadeIn.playFromStart();
        });
    
        button.setOnMouseExited(event -> {
            fadeIn.stop();
            fadeOut.playFromStart();
        });
    }

    private void showPopup(String message) {
        popupMessage.setText(message);
        popupPane.setVisible(true);
        popupPane.setManaged(true);
        popupPane.toFront(); // Ensures it stays on top
    
        setBoardInteraction(false); // Disable interaction
    }
    
    @FXML
    private void closePopup() {
        popupPane.setVisible(false);
        popupPane.setManaged(false);
    
        setBoardInteraction(true); // Re-enable interaction
    }

    private void startGame() {
        turnSelectionPopup.setVisible(false);
        setBoardInteraction(true); // Allow interaction once popup is gone
    
        if (!playerGoesFirst) aiFirstMove(); // AI picks a random corner if it goes first
    }

    private void showTurnSelectionPopup() {
        turnSelectionPopup.setVisible(true);
        setBoardInteraction(false); // Disable interaction while choosing who goes first
    }

    private void setBoardInteraction(boolean enabled) {
        for (Button button : buttons) {
            button.setDisable(!enabled); // Disable/enable clicking
        }
        for (Label label : labels) {
            label.setVisible(enabled); // Disable hover effect
        }
    }

    private void aiFirstMove() {
        // AI chooses a random corner: (0,0), (0,2), (2,0), (2,2)
        int[] corners = {0, 2, 6, 8};
        int randomIndex = new Random().nextInt(4);
        int move = corners[randomIndex];
    
        int row = move / 3;
        int col = move % 3;
    
        board[row][col] = 'O';
        buttons[move].getStyleClass().add("o");
        buttons[move].setText("O");
        labels[move].setVisible(false);
    }
    
}
