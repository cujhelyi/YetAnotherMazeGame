package com.example.labyrinth.entity;

import jakarta.persistence.*;

@Entity
public class BoardTile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rowIndex;

    @Column(nullable = false)
    private int colIndex;

    // Merged MapPiece fields
    @Column(nullable = false)
    private boolean exitNorth;

    @Column(nullable = false)
    private boolean exitEast;

    @Column(nullable = false)
    private boolean exitSouth;

    @Column(nullable = false)
    private boolean exitWest;

    @Column(nullable = false)
    private String treasure;

    @Column
    private String image;

    @Column(nullable = false)
    private boolean locked = false;

    @ManyToOne(optional = false)
    private GameBoard gameBoard;

    public Long getId() {
        return id;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public boolean isExitNorth() {
        return exitNorth;
    }

    public void setExitNorth(boolean exitNorth) {
        this.exitNorth = exitNorth;
    }

    public boolean isExitEast() {
        return exitEast;
    }

    public void setExitEast(boolean exitEast) {
        this.exitEast = exitEast;
    }

    public boolean isExitSouth() {
        return exitSouth;
    }

    public void setExitSouth(boolean exitSouth) {
        this.exitSouth = exitSouth;
    }

    public boolean isExitWest() {
        return exitWest;
    }

    public void setExitWest(boolean exitWest) {
        this.exitWest = exitWest;
    }

    /**
     * Rotate this tile clockwise.
     *
     * Note: per project convention, a single "clockwise" rotation maps exits as follows:
     * north -> west, east -> north, south -> east, west -> south.
     * This behavior matches the requested mapping where a tile with only north=true
     * becomes only west=true after rotateClockwise().
     */
    public void rotateClockwise() {
        boolean oldN = this.exitNorth;
        boolean oldE = this.exitEast;
        boolean oldS = this.exitSouth;
        boolean oldW = this.exitWest;

        this.exitNorth = oldE; // east becomes north
        this.exitEast = oldS;  // south becomes east
        this.exitSouth = oldW; // west becomes south
        this.exitWest = oldN;  // north becomes west
    }

    /** Rotate clockwise multiple times (times mod 4). */
    public void rotateClockwise(int times) {
        int t = ((times % 4) + 4) % 4;
        for (int i = 0; i < t; i++) rotateClockwise();
    }

    /** Rotate counter-clockwise once. */
    public void rotateCounterClockwise() {
        boolean oldN = this.exitNorth;
        boolean oldE = this.exitEast;
        boolean oldS = this.exitSouth;
        boolean oldW = this.exitWest;

        this.exitNorth = oldW; // west becomes north
        this.exitEast = oldN;  // north becomes east
        this.exitSouth = oldE; // east becomes south
        this.exitWest = oldS;  // south becomes west
    }

    /** Rotate counter-clockwise multiple times (times mod 4). */
    public void rotateCounterClockwise(int times) {
        int t = ((times % 4) + 4) % 4;
        for (int i = 0; i < t; i++) rotateCounterClockwise();
    }

    public String getTreasure() {
        return treasure;
    }

    public void setTreasure(String treasure) {
        this.treasure = treasure;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
