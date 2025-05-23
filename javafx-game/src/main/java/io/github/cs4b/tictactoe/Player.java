package tictactoe;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final char symbol;

    public Player(Socket socket, ObjectInputStream in, ObjectOutputStream out, char symbol) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.symbol = symbol;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public char getSymbol() {
        return symbol;
    }

    public void close() {
        try {
            if (in != null) in.close();
        } catch (Exception ignored) {}
        try {
            if (out != null) out.close();
        } catch (Exception ignored) {}
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
