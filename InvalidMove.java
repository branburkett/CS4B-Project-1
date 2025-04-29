package io.github.cs4b.tictactoe;

public class InvalidMove extends Message {
    public InvalidMove(String message) {
        super(MessageType.INVALID_MOVE, message);
    }
}