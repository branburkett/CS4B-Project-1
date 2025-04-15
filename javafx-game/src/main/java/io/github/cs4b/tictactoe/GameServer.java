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

    public static void main(String[] args) throws IOException {
        new GameServer().start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Game server started...");

        while (clients.size() < 2) {
            Socket socket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            clients.add(out);
            out.writeObject(new Message(Message.MessageType.PLAYER_CONNECTED, "You are player " + (clients.size() == 1 ? "X" : "O")));

            new Thread(() -> handleClient(in, out)).start();
        }
    }

    private void handleClient(ObjectInputStream in, ObjectOutputStream out) {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                if (msg.type == Message.MessageType.MOVE_MADE) {
                    String[] parts = msg.payload.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    char symbol = parts[2].charAt(0);
    
                    if (board[row][col] == '\0' && symbol == currentTurn) {
                        board[row][col] = symbol;
                        broadcast(new Message(Message.MessageType.MOVE_MADE, row + "," + col + "," + symbol));
    
                        if (checkWin(symbol)) {
                            broadcast(new Message(Message.MessageType.GAME_OVER, symbol + " wins!"));
                        } else if (isBoardFull()) {
                            broadcast(new Message(Message.MessageType.GAME_OVER, "Draw!"));
                        } else {
                            currentTurn = (symbol == 'X') ? 'O' : 'X';
                            broadcast(new Message(Message.MessageType.TURN_CHANGE, "It's player " + currentTurn + "'s turn"));
                        }
                    } else {
                        out.writeObject(new Message(Message.MessageType.INVALID_MOVE, "Invalid move"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
