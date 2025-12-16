package com.example.labyrinth.entity;
import jakarta.persistence.*;

@Entity
public class MapPiece {
    
    @Id
    private Integer id;

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

    /**
     * Deprecated: MapPiece has been merged into BoardTile. This class remains as a
     * placeholder for migration compatibility and should not be used.
     */
    @Deprecated
    public MapPiece() {
        throw new UnsupportedOperationException("MapPiece is deprecated; use BoardTile instead");
    }
    public MapPiece(Integer id, boolean exitNorth, boolean exitEast, boolean exitSouth, boolean exitWest, String treasure) {
        throw new UnsupportedOperationException("MapPiece is deprecated; use BoardTile instead");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTreasure() {
        return treasure;
    }

    public void setTreasure(String treasure) {
        this.treasure = treasure;
    }
}
