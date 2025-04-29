package io.github.cs4b.tictactoe;

public class GameStart extends Message {
    public GameStart(String message) {
        super(MessageType.GAME_START, message);
    }
}