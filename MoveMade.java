package io.github.cs4b.tictactoe;

public class MoveMade extends Message {
    private int row;
    private int col;
    private char symbol;

    public MoveMade(int row, int col, char symbol) {
        super(MessageType.MOVE_MADE, row + "," + col + "," + symbol);
        this.row = row;
        this.col = col;
        this.symbol = symbol;
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