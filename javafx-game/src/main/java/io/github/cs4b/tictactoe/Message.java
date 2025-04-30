package tictactoe;
import java.io.Serializable;

public class Message implements Serializable {
    public enum MessageType {
        PLAYER_CONNECTED {
            @Override
            public String toString() {
                return "Player Connected";
            }
        },
        MOVE_MADE {
            @Override
            public String toString() {
                return "Move Made";
            }
        },
        INVALID_MOVE {
            @Override
            public String toString() {
                return "Invalid Move";
            }
        },
        TURN_CHANGE {
            @Override
            public String toString() {
                return "Turn Change";
            }
        },
        GAME_START {
            @Override
            public String toString() {
                return "Game Started";
            }
        },
        GAME_OVER {
            @Override
            public String toString() {
                return "Game Over";
            }
        },
        PLAYER_DISCONNECTED {
            @Override
            public String toString() {
                return "Player Disconnected";
            }
        }
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
