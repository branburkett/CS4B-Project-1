package tictactoe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static final int PORT = 12345;
    private final char[][] board = new char[3][3];
    private final List<Player> players = new ArrayList<>();     // To keep track of all players, would be useful to keep if scaling was needed
    private Player xPlayer, oPlayer;    // Used to have an easy way to keep access of specific players
    private int xWins, oWins, draws;
    private int rematchTally = 0;
    private boolean xStarts = true;
    private char currentTurn = 'X';

    public static void main(String[] args) throws IOException {
        GameServer gameServer = new GameServer();
        gameServer.start();
    }

    // In a loop, accepts up to 2 player connections and assigns them their symbol, and creates seperate threads to listen to them.
    // If a disconnection happens, it breaks out of the first iteration of the loop and waits for 2 player connections again (since both initial connections will be reset)
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT); // Sets up socket for players to connect to

        while (true) {
            players.clear();    // Clears previous connections
            xPlayer = null;
            oPlayer = null;
            rematchTally = 0;

            while (players.size() < 2) {

                // Waits until it receives a player connection
                Socket socket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());      // Stores the player's stream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                Player player = new Player (socket, in, out, ((players.size() == 1) ? 'X' : 'O')); // Creates a player so it can be added to the player
                players.add(player);                                                               // list and assigned to its respective player variable

                // Lets incoming players know it's received their connection and assigns them a symbol
                if (players.size() == 1) {
                    xPlayer = player;
                    out.writeObject(new Message.PlayerConnected("X"));  
                } else {
                    oPlayer = player;
                    out.writeObject(new Message.PlayerConnected("O"));    
                }
                new Thread(() -> handleClient(player)).start();     // Creates a separate thread to listen to players' message
            }

            startNewGame(); // Sets up board and logic on server side and messages players that game has started

            // Wait for a disconnect before restarting the loop
            while (players.size() == 2) {
                try {
                    Thread.sleep(500); // Used so that it's not constantly checking for a disconnection
                } catch (InterruptedException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    // Listens to messages from the player, and carries out actions depending on the message
    private void handleClient(Player player) {
        try {
            while (true) {
                Message msg = (Message) player.getIn().readObject(); // Reads in message
                System.out.println(msg.type.toString());
                switch (msg.type) {
                
                    // Checks if that space is already occupied, if not sends a move confirmation message
                    // and checks for a win or draw and will send a message for that if either is true
                    case MOVE_MADE:
                        String[] parts = msg.payload.split(",");
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);
                        char symbol = parts[2].charAt(0);

                        // Checking if space is not already taken
                        if (board[row][col] == '\0' && symbol == currentTurn) {
                            board[row][col] = symbol;
                            broadcast(new Message.MoveMade(row + "," + col + "," + symbol));
                            
                            // If theres a win, assign a point to whoever's turn it was
                            if (checkWin(symbol)) {
                                xWins = (currentTurn == 'X') ? ++xWins : xWins;
                                oWins = (currentTurn == 'O') ? ++oWins : oWins;
                                broadcast(new Message.GameOver(String.valueOf(currentTurn) + "," + ((currentTurn == 'X') ? xWins : oWins)));

                            // If theres a draw, add a point to the draw
                            } else if (isBoardFull()) {
                                draws++;
                                broadcast(new Message.GameOver("Draw" + "," + String.valueOf(draws)));
                            } else {
                                currentTurn = (symbol == 'X') ? 'O' : 'X';
                                //broadcast(new Message.TurnChange());
                            }
                        
                        // Otherwise it's an invalid move
                        }else {
                            player.getOut().writeObject(new Message(Message.MessageType.INVALID_MOVE, "Invalid move"));
                        }
                        break;
                    
                    // Only time server will receive this message is if a player requests a rematch, 
                    // so if it receives two of these messages, it resets board and starts a new game
                    case GAME_START:
                        ++rematchTally;
                        if (rematchTally == 2) {
                            rematchTally = 0;
                            resetBoard();
                            startNewGame();
                        }
                        break;
                    
                    case PLAYER_DISCONNECTED:
                        handleDisconnect(player); // Clears the connection of the player who left, and notifies the other player a disconnect has happened
                        resetGame(); // Resets all game logic
                        break;
                    }

            }
        
        // Catches a disconnect that occurs when a disconnect message can't be sent
        } catch (Exception e) {
            handleDisconnect(player);
            resetGame();
        }
    }

    // Sends a message to all players
    private void broadcast(Message message) {
        for (Player client : players) {
            try {
                client.getOut().writeObject(message);
                client.getOut().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Used for rematches where you want to keep some of the data from previous games
    private void startNewGame() {
        resetBoard();
        currentTurn = xStarts ? 'X' : 'O';
        xStarts = !xStarts;
        broadcast(new Message.GameStart(String.valueOf(currentTurn)));

    }

    // Clears the connection of the player who left, and notifies the other player that a disconnect has happened
    private void handleDisconnect(Player player) {
        Player otherPlayer = (player == xPlayer) ? oPlayer: xPlayer; // Gets the player to notify
        try {
            if (otherPlayer != null) { // If other player is still there, send them a DC message
                otherPlayer.getOut().writeObject(new Message.PlayerDC());
                otherPlayer.getOut().flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        players.remove(player); // Remove disconnected player connection
    }

    // Used for new games started from the landing screen, makes
    // sure all previous connections and data from previous games are reset
    private void resetGame() {
        if (xPlayer != null) xPlayer.close();
        if (oPlayer != null) oPlayer.close();

        xPlayer = null;     // Clears all connections
        oPlayer = null;
        players.clear();

        rematchTally = 0;
        resetBoard();
        xWins = 0;
        oWins = 0;
        draws = 0;
        currentTurn = 'X';
        xStarts = true;
    }

    // Checks if current board has a win
    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)) return true;
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
               (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    //Checks if current board is full
    private boolean isBoardFull() {
        for (char[] row : board)
            for (char c : row)
                if (c == '\0') return false;
        return true;
    }

    // Clears the board
    private void resetBoard() {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++) 
                board[i][j] = '\0';      
    }
}