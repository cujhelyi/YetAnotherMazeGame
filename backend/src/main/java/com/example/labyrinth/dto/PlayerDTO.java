package com.example.labyrinth.dto;

public class PlayerDTO {
    private Long id;
    private int playerNumber;
    private int rowIndex;
    private int colIndex;

    public PlayerDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getPlayerNumber() { return playerNumber; }
    public void setPlayerNumber(int playerNumber) { this.playerNumber = playerNumber; }
    
    public int getRowIndex() { return rowIndex; }
    public void setRowIndex(int rowIndex) { this.rowIndex = rowIndex; }
    
    public int getColIndex() { return colIndex; }
    public void setColIndex(int colIndex) { this.colIndex = colIndex; }
}
