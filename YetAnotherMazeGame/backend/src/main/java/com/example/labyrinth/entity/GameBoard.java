package com.example.labyrinth.entity;
import jakarta.persistence.*;

@Entity
public class GameBoard {
    private static final int BOARD_SIZE = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private MapPiece[][] boardArray = new MapPiece[BOARD_SIZE][BOARD_SIZE];

    @Transient
    private MapPiece sparePiece;

    @Transient
    private java.util.List<String> players = new java.util.ArrayList<>();

    private int[] defaultPieces = {1,2,3,4,5,6,7,8,9,10};

    public MapPiece[][] getBoardArray() {
        return boardArray;
    }

    public void resetBoardArray() {
        this.boardArray = new MapPiece[BOARD_SIZE][BOARD_SIZE];
    }

    private void setDefaults() {
        int idCounter = 1;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                MapPiece piece = new MapPiece();
                piece.setId(idCounter++);
                piece.setExitNorth(false);
                piece.setExitEast(false);
                piece.setExitSouth(false);
                piece.setExitWest(false);
                piece.setTreasure("");
                this.boardArray[i][j] = piece;
            }
        }
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }
}
