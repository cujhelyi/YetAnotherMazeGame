package com.example.labyrinth.entity;
import jakarta.persistence.*;

@Entity
public class GameBoard {
    private static final int BOARD_SIZE = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private BoardTile[][] boardArray = new BoardTile[BOARD_SIZE][BOARD_SIZE];


    @Transient
    private BoardTile sparePiece;

    @Transient
    private java.util.List<String> players = new java.util.ArrayList<>();

    private int[] defaultPieces = {1,2,3,4,5,6,7,8,9,10};

    @OneToMany(mappedBy = "gameBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<BoardTile> tiles = new java.util.ArrayList<>();

    public Long getId() {
        return id;
    }

    public void resetBoardArray() {
        // Initialize the board array and spare piece
        this.boardArray = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        this.sparePiece = null;
        this.clearTiles();
    }

    public void setDefaults() {
        // Fill every cell with a simple default BoardTile (in-memory).
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                BoardTile t = new BoardTile();
                t.setRowIndex(i);
                t.setColIndex(j);
                t.setExitNorth(i % 2 == 0);
                t.setExitEast(j % 2 == 0);
                t.setExitSouth(i % 2 == 1);
                t.setExitWest((i + j) % 3 == 0);
                t.setTreasure("");
                this.boardArray[i][j] = t;
            }
        }
    }

    public BoardTile[][] getBoardArray() {
        return this.boardArray;
    }

    public java.util.List<BoardTile> getTiles() {
        return tiles;
    }

    public void addTile(BoardTile tile) {
        tile.setGameBoard(this);
        this.tiles.add(tile);
    }

    public void clearTiles() {
        this.tiles.clear();
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }
}
