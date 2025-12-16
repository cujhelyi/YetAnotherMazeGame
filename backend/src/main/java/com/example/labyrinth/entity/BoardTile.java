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

    @Column(nullable = false)
    private int rotation = 0;

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

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void rotateClockwise() {
        boolean oldN = this.exitNorth;
        boolean oldE = this.exitEast;
        boolean oldS = this.exitSouth;
        boolean oldW = this.exitWest;

        this.exitNorth = oldE;
        this.exitEast = oldS; 
        this.exitSouth = oldW;
        this.exitWest = oldN; 
        
        int clockwiseRotatedAngle = (this.rotation - 90 + 360) % 360;
        this.rotation = clockwiseRotatedAngle;
    }

    public void rotateClockwise(int times) {
        int numClockwiseRotations = ((times % 4) + 4) % 4;

        for (int i = 0; i < numClockwiseRotations; i++) rotateClockwise();
    }

    public void rotateCounterClockwise() {
        boolean oldN = this.exitNorth;
        boolean oldE = this.exitEast;
        boolean oldS = this.exitSouth;
        boolean oldW = this.exitWest;

        this.exitNorth = oldW; 
        this.exitEast = oldN;  
        this.exitSouth = oldE; 
        this.exitWest = oldS;  
        
        int counterClockwiseRotatedAngle = (this.rotation + 90) % 360;
        this.rotation = counterClockwiseRotatedAngle;
    }

    public void rotateCounterClockwise(int times) {
        int numCounterClockwiseRotations = ((times % 4) + 4) % 4;

        for (int i = 0; i < numCounterClockwiseRotations; i++) rotateCounterClockwise();
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
