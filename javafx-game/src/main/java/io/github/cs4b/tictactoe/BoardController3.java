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
    private Button backButton;
    @FXML
    private Label playerXScore, drawScore, playerOScore;
    @FXML
    private Button[] buttons;
    private Label[] labels;
    @FXML
    private Label buttonHover1, buttonHover2, buttonHover3, buttonHover4, buttonHover5, buttonHover6, buttonHover7, buttonHover8, buttonHover9;

    private boolean gameActive = true;

    private NetworkManager networkManager;
    private char mySymbol;
    private volatile boolean myTurn;            // Using volatile because it will be modified by
    private volatile boolean running = true;    // different threads

    // Passes the connection made in the loading screen to this controller, and starts listening to messages
    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
        running = true;
        new Thread(this::listenToServer).start(); // Start listening for messages
    }

    // Passes the initial statusreceived by the server in the loading screen to the controller
    public void setInitStatus(char symbol, boolean isYourTurn) {
        this.mySymbol = symbol;
        this.myTurn = isYourTurn;
        Platform.runLater(() -> {                          
            for (int i = 0; i < buttons.length; i++) {      
                setupButtonHover(buttons[i], labels[i]);    // Initialize the ButtonHover effect here because the player
              }  // symbol is not assigned until we call this function
        });
    }

    // Sets up the game board and button connections
    @FXML
    public void initialize() {
        buttons = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};
        labels = new Label[]{buttonHover1, buttonHover2, buttonHover3, buttonHover4, buttonHover5, buttonHover6, buttonHover7, buttonHover8, buttonHover9};
        resetBoard();

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnAction(e -> handlePlayerMove(index));
        }

        newGame.setOnAction(e -> requestRematch());
        backButton.setOnAction(e -> goToMainMenu());
    }
    
    // When a button is pressed on the board, a move requset is sent to the server
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

    // Updates the board from a move confirmation message from the server
    private void updateCellFromServer(String payload) {
        String[] parts = payload.split(",");    // Seperating the message payload
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        char symbol = parts[2].charAt(0);

        int index = row * 3 + col;
 
        buttons[index].setText(String.valueOf(symbol)); // Updates board with the symbol 
                                                        // of whichever player made the move
    
        // Add style based on the symbol (X or O)
        buttons[index].getStyleClass().add(symbol == 'X' ? "x" : "o");
    }
    
    // Listens to messages from server, and carries out actions depending on the message
    private void listenToServer() {
        try {
            while (running) {
                Message msg = networkManager.receiveMessage();
    
                switch (msg.type) {

                    // Update board according to move, switch turns, and turn off hover effect
                    case MOVE_MADE:
                         Platform.runLater(() -> {              
                            updateCellFromServer(msg.payload);
                            myTurn = !myTurn;
                            toggleLabels();
                         });
                        break;

                    // Update score and disable move making
                    case GAME_OVER:
                        Platform.runLater(() -> updateScores(msg.payload)); // Update score UI on JavaFX thread
                        myTurn = false;
                        break;
                    case INVALID_MOVE:
                        break;
                    
                    // If a rematch occurs, set status received by server
                    case GAME_START:
                        char startingTurn = msg.payload.charAt(0);
                        myTurn = (mySymbol == startingTurn);
                    
                        Platform.runLater(() -> resetBoard()); // Reset board on JavaFX thread
                        break;

                    // Cleans up the thread listening for messages, closes client connection, and takes client back to the landing screen
                    case PLAYER_DISCONNECTED:
                        running = false; // Makes sure that the thread running listenToServer breaks out of loop
                        networkManager.close();
                        Platform.runLater(() -> TicTacToeApp.showLandingScreen()); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Closes thread listening to server and the client connection, and lets server
    // know that client is disconnecting when returning to landing screen
    @FXML
    private void goToMainMenu() {
        try {
            running = false; // Makes sure that the thread running listenToServer breaks out of loop
            networkManager.sendMessage(new Message.PlayerDC());
            networkManager.close();
        } catch (Exception e) {

        }
        TicTacToeApp.showLandingScreen();
    }

    // Updates scores based on server message
    private void updateScores(String payload) {
        String[] parts = payload.split(",");    // Breaks up message into the type
        String type = parts[0];                       // of score and its score
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

    // Tells the server that the client wants a rematch
    private void requestRematch() {
        try {
            networkManager.sendMessage(new Message.GameStart(""));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    // Empties the board, and sets up the hover effect to initially be on if its their turn
    private void resetBoard() {
        gameActive = true;
    
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].getStyleClass().removeAll("x", "o");
            labels[i].setVisible(myTurn);
            setupButtonHover(buttons[i], labels[i]);
        }
        toggleLabels();
    }

    // Sets up the button hover effect
    private void setupButtonHover(Button button, Label label) {
        label.setOpacity(0.0);
        label.setMouseTransparent(true);
        label.setText(String.valueOf(mySymbol));
        label.setVisible(myTurn);
    
        // Sets up the hover fade effect
        FadeTransition fadeIn = new FadeTransition(Duration.millis(160), label);
        fadeIn.setToValue(0.5);
    
        FadeTransition fadeOut = new FadeTransition(Duration.millis(175), label);
        fadeOut.setToValue(0.0);

        // Assigns the hover effect to each button
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

    // Toggles the hover effect visibility
    private void toggleLabels() {
        for (int i = 0; i < 9; i++) {
            labels[i].setVisible(myTurn);
        }
    }
}
