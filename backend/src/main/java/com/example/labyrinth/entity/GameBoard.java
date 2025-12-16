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

    @OneToMany(mappedBy = "gameBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<BoardTile> tiles = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "gameBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Player> playersList = new java.util.ArrayList<>();

    public Long getId() {
        return id;
    }

    public void resetBoardArray() {
        this.boardArray = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        this.sparePiece = null;
        this.clearTiles();
    }

    public void setSparePiece(BoardTile sparePiece) {
        this.sparePiece = sparePiece;
    }

    public BoardTile getSparePiece() {
        return this.sparePiece;
    }

    public void populateFromTiles() {
        this.boardArray = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        this.sparePiece = null;
        for (BoardTile tile : this.tiles) {
            int row = tile.getRowIndex();
            int col = tile.getColIndex();
            if (row < 0 || col < 0) {
                this.sparePiece = tile;
            } else if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                this.boardArray[row][col] = tile;
            }
        }
    }

    public void setDefaults() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                BoardTile tile = new BoardTile();
                tile.setRowIndex(i);
                tile.setColIndex(j);
                tile.setExitNorth(i % 2 == 0);
                tile.setExitEast(j % 2 == 0);
                tile.setExitSouth(i % 2 == 1);
                tile.setExitWest((i + j) % 3 == 0);
                tile.setTreasure("");
                this.boardArray[i][j] = tile;
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

    public java.util.List<Player> getPlayersList() {
        return playersList;
    }

    public void addPlayer(Player player) {
        player.setGameBoard(this);
        this.playersList.add(player);
    }
}
