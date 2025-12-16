package com.example.labyrinth.dto;

public class TileDTO {
    private Long id;
    private int rowIndex;
    private int colIndex;
    private boolean exitNorth;
    private boolean exitEast;
    private boolean exitSouth;
    private boolean exitWest;
    private String treasure;
    private String image;
    private int rotation;

    public TileDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getRowIndex() { return rowIndex; }
    public void setRowIndex(int rowIndex) { this.rowIndex = rowIndex; }

    public int getColIndex() { return colIndex; }
    public void setColIndex(int colIndex) { this.colIndex = colIndex; }

    public boolean isExitNorth() { return exitNorth; }
    public void setExitNorth(boolean exitNorth) { this.exitNorth = exitNorth; }

    public boolean isExitEast() { return exitEast; }
    public void setExitEast(boolean exitEast) { this.exitEast = exitEast; }

    public boolean isExitSouth() { return exitSouth; }
    public void setExitSouth(boolean exitSouth) { this.exitSouth = exitSouth; }

    public boolean isExitWest() { return exitWest; }
    public void setExitWest(boolean exitWest) { this.exitWest = exitWest; }

    public String getTreasure() { return treasure; }
    public void setTreasure(String treasure) { this.treasure = treasure; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getRotation() { return rotation; }
    public void setRotation(int rotation) { this.rotation = rotation; }
}
