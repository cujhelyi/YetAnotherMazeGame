package com.example.labyrinth;

import com.example.labyrinth.entity.BoardTile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTileRotationTests {

    @Test
    public void singleClockwiseRotation_NorthBecomesWest() {
        BoardTile t = new BoardTile();
        t.setExitNorth(true);
        t.setExitEast(false);
        t.setExitSouth(false);
        t.setExitWest(false);

        t.rotateClockwise();

        assertFalse(t.isExitNorth());
        assertFalse(t.isExitEast());
        assertFalse(t.isExitSouth());
        assertTrue(t.isExitWest(), "north should become west after one clockwise rotation");
    }

    @Test
    public void fourRotations_ReturnsToOriginal() {
        BoardTile t = new BoardTile();
        t.setExitNorth(true);
        t.setExitEast(true);
        t.setExitSouth(false);
        t.setExitWest(false);
        String treasure = "TEST";
        t.setTreasure(treasure);

        t.rotateClockwise(4);

        assertTrue(t.isExitNorth());
        assertTrue(t.isExitEast());
        assertFalse(t.isExitSouth());
        assertFalse(t.isExitWest());
        assertEquals(treasure, t.getTreasure(), "treasure should be unchanged after rotations");
    }

    @Test
    public void twoRotations_NorthBecomesSouth() {
        BoardTile t = new BoardTile();
        t.setExitNorth(true);
        t.setExitEast(false);
        t.setExitSouth(false);
        t.setExitWest(false);

        t.rotateClockwise(2);

        assertFalse(t.isExitNorth());
        assertFalse(t.isExitEast());
        assertTrue(t.isExitSouth(), "north should become south after two clockwise rotations");
        assertFalse(t.isExitWest());
    }

    @Test
    public void singleCounterClockwise_NorthBecomesEast() {
        BoardTile t = new BoardTile();
        t.setExitNorth(true);
        t.setExitEast(false);
        t.setExitSouth(false);
        t.setExitWest(false);

        t.rotateCounterClockwise();

        assertFalse(t.isExitNorth());
        assertTrue(t.isExitEast(), "north should become east after one counter-clockwise rotation");
        assertFalse(t.isExitSouth());
        assertFalse(t.isExitWest());
    }
}
