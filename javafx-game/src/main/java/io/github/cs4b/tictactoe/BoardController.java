// BoardController.java is the Local 2-Player option

package io.github.cs4b.tictactoe;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class BoardController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gamePane;

    @FXML
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @FXML
    private Button newGame, backButton;

    @FXML
    private Label playerXScore, drawScore, playerOScore;

    @FXML
    private Button[] buttons;

    private char[][] board = new char[3][3]; 
    private boolean gameActive = true;
    private boolean isPlayerXTurn = true; // Track player turns
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
    }

    @FXML
    private void goToMainMenu() {
        TicTacToeApp.showLandingScreen();
    }

    private void handlePlayerMove(int index) {
        if (!gameActive || !buttons[index].getText().isEmpty()) return;

        int row = index / 3, col = index % 3;
        char currentPlayer = isPlayerXTurn ? 'X' : 'O';

        board[row][col] = currentPlayer;
        buttons[index].setText(String.valueOf(currentPlayer));
        buttons[index].getStyleClass().add(isPlayerXTurn ? "x" : "o");

        labels[index].setVisible(false);

        if (checkWin(currentPlayer)) {
            for (int i = 0; i < 9; i++) {
                labels[i].setVisible(false);
            }
            if (currentPlayer == 'X') xWins++;
            else oWins++;
            updateScores();
            gameActive = false;
            return;
        }

        if (isBoardFull()) {
            draws++;
            updateScores();
            gameActive = false;
            return;
        }

        isPlayerXTurn = !isPlayerXTurn; // Switch turns

        // Update buttonHovers to correct icon
        for (Label label : labels) {      
            label.setText((isPlayerXTurn) ? "X" : "O");
        }
    }

    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)) 
                return true;
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

    private void updateScores() {
        playerXScore.setText(String.valueOf(xWins));
        drawScore.setText(String.valueOf(draws));
        playerOScore.setText(String.valueOf(oWins));
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++) 
                board[i][j] = ' ';

        for (Button button : buttons) {
            button.setText("");
            button.getStyleClass().removeAll("x", "o");
        }
        for (int i = 0; i < 9; i++) {
            setupButtonHover(buttons[i], labels[i]);
        }
        
        gameActive = true;
        isPlayerXTurn = true; // X starts first
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
}
