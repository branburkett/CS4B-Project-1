package tictactoe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkManager {
    Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public NetworkManager(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }
    public void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (Exception e) {
        }
        socket = null;
        in = null;
        out = null;
    }
}
