package io.github.cs4b.tictactoe;

public class PlayerConnected extends Message {
    public PlayerConnected(String message) {
        super(MessageType.PLAYER_CONNECTED, message);
    }
}
