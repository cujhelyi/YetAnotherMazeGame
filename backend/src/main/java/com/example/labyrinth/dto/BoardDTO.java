package com.example.labyrinth.dto;

import java.util.List;

public class BoardDTO {
    private Long id;
    private int size;
    private List<TileDTO> tiles;
    private List<PlayerDTO> players;

    public BoardDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public List<TileDTO> getTiles() { return tiles; }
    public void setTiles(List<TileDTO> tiles) { this.tiles = tiles; }
    
    public List<PlayerDTO> getPlayers() { return players; }
    public void setPlayers(List<PlayerDTO> players) { this.players = players; }
}
