package io.github.cs4b.tictactoe;
import java.io.Serializable;

public class Message implements Serializable {
    public enum MessageType {
        PLAYER_CONNECTED,
        MOVE_MADE,
        INVALID_MOVE,
        TURN_CHANGE,
        GAME_START,
        GAME_OVER,
        PLAYER_DISCONNECTED
    }

    public MessageType type;
    public String payload;
    public boolean isYourTurn;
    public Message(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public Message(String payload) {
        this.payload = payload;
    }

    public Message(String payload, boolean isYourTurn) {
        this.payload = payload;
        this.isYourTurn = isYourTurn;
    }
    // Constructor to include turn status
    public Message(MessageType type, String payload, boolean isYourTurn) {
        this.type = type;
        this.payload = payload;
        this.isYourTurn = isYourTurn;
    }
}

