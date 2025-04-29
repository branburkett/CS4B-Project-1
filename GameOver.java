package io.github.cs4b.tictactoe;

public class GameOver extends Message {
    public GameOver(String resultMessage) {
        super(MessageType.GAME_OVER, resultMessage);
    }
}