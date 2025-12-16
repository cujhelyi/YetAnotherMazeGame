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

    @ManyToOne(optional = false)
    private MapPiece mapPiece;

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

    public MapPiece getMapPiece() {
        return mapPiece;
    }

    public void setMapPiece(MapPiece mapPiece) {
        this.mapPiece = mapPiece;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
