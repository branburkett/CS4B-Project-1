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
    private final List<ObjectOutputStream> clients = new ArrayList<>();
    private char currentTurn = 'X';
    private int xWins, oWins, draws;
    private int rematchTally = 0;
    private boolean xStarts = true;

    public static void main(String[] args) throws IOException {
        GameServer gameServer = new GameServer();
        gameServer.start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Game server started...");

        while (clients.size() < 2) {
            Socket socket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            clients.add(out);
            out.writeObject(new Message.PlayerConnected((clients.size() == 1 ? "X" : "O")));

            new Thread(() -> handleClient(in, out)).start();
        }
        startNewGame();
    }

    private void handleClient(ObjectInputStream in, ObjectOutputStream out) {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                System.out.println(msg.type.toString());
                if (msg.type == Message.MessageType.MOVE_MADE) {
                    String[] parts = msg.payload.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    char symbol = parts[2].charAt(0);

    
                    if (board[row][col] == '\0' && symbol == currentTurn) {
                        board[row][col] = symbol;
                        broadcast(new Message.MoveMade(row + "," + col + "," + symbol));
    
                        if (checkWin(symbol)) {
                            xWins = (currentTurn == 'X') ? ++xWins : xWins;
                            oWins = (currentTurn == 'O') ? ++oWins : oWins;
                            broadcast(new Message.GameOver(String.valueOf(currentTurn) + "," + ((currentTurn == 'X') ? xWins : oWins)));
                        } else if (isBoardFull()) {
                            draws++;
                            broadcast(new Message.GameOver("Draw" + "," + String.valueOf(draws)));
                        } else {
                            currentTurn = (symbol == 'X') ? 'O' : 'X';
                            //broadcast(new Message.TurnChange());
                        }
                    }else {
                        out.writeObject(new Message(Message.MessageType.INVALID_MOVE, "Invalid move"));
                    }
                }
                else if (msg.type == Message.MessageType.GAME_START) {
                    //tally one rematch vote 
                    ++rematchTally;
                    // if two rematch votes, send game start message and reset rematch vote
                    if (rematchTally == 2) {
                        System.out.println("restarting game");
                        resetBoard();
                        rematchTally = 0;
                        startNewGame();
                    }
                } 
            }
        } catch (Exception e) {
            System.out.println("Player disconnected");
            
        }
    }

    private void broadcast(Message message) {
        for (ObjectOutputStream client : clients) {
            try {
                client.writeObject(message);
                client.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startNewGame() {
        resetBoard();
        currentTurn = xStarts ? 'X' : 'O';
        xStarts = !xStarts;
        broadcast(new Message.GameStart(String.valueOf(currentTurn)));

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
        for (char[] row : board)
            for (char c : row)
                if (c == '\0') return false;
        return true;
    }
    private void resetBoard() {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++) 
                board[i][j] = '\0';      
    }
}