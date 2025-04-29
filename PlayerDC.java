package io.github.cs4b.tictactoe;

public class PlayerDC extends Message {
    public PlayerDC(String message) {
        super(MessageType.PLAYER_DISCONNECTED, message);
    }
}
