package tictactoe;
import java.io.Serializable;

public class Message implements Serializable {
    public enum MessageType {
        // remove toString override when done, used just for tseting
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

    public static class GameOver extends Message {
        public GameOver(String resultMessage) {
            super(MessageType.GAME_OVER, resultMessage);
        }
    }
    public static class GameStart extends Message {
        public GameStart(String message) {
            super(MessageType.GAME_START, message);
        }
    }
    public static class InvalidMove extends Message {
        public InvalidMove(String message) {
            super(MessageType.INVALID_MOVE, message);
        }
    }
    public static class MoveMade extends Message {
        private int row;
        private int col;
        private char symbol;

        public MoveMade(int row, int col, char symbol) {
            super(MessageType.MOVE_MADE, row + "," + col + "," + symbol);
            this.row = row;
            this.col = col;
            this.symbol = symbol;
        }
        public MoveMade(String message) {
            super(MessageType.MOVE_MADE, message);
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public char getSymbol() {
            return symbol;
        }
    }
    public static class PlayerConnected extends Message {
        public PlayerConnected(String message) {
            super(MessageType.PLAYER_CONNECTED, message);
        }
    }
    public static class PlayerDC extends Message {
        public PlayerDC(String message) {
            super(MessageType.PLAYER_DISCONNECTED, message);
        }
    }
}
