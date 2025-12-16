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

    @OneToMany(mappedBy = "gameBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<BoardTile> tiles = new java.util.ArrayList<>();

    public MapPiece[][] getBoardArray() {
        return boardArray;
    }

    public Long getId() {
        return id;
    }

    public void resetBoardArray() {
        this.boardArray = new MapPiece[BOARD_SIZE][BOARD_SIZE];
    }

    public void setDefaults() {
        // Fill every cell with a simple default MapPiece (in-memory). Persistence should
        // be handled by the BoardService which will create BoardTile entities that
        // reference persisted MapPiece prototypes.
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // use id 0 as an ephemeral in-memory piece (not persisted)
                this.boardArray[i][j] = new MapPiece(0, false, false, false, false, "");
            }
        }
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
