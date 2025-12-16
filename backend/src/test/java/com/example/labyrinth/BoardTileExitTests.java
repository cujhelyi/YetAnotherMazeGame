package com.example.labyrinth;

import com.example.labyrinth.entity.BoardTile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTileExitTests {

    @Test
    public void testExitRotationClockwise() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(false);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateClockwise();

        assertFalse(tile.isExitNorth());
        assertFalse(tile.isExitEast());
        assertFalse(tile.isExitSouth());
        assertTrue(tile.isExitWest());
    }

    @Test
    public void testExitRotationCounterClockwise() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(false);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateCounterClockwise();

        assertFalse(tile.isExitNorth());
        assertTrue(tile.isExitEast());
        assertFalse(tile.isExitSouth());
        assertFalse(tile.isExitWest());
    }

    @Test
    public void testMultipleExitsRotateClockwise() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(true);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateClockwise();

        assertTrue(tile.isExitNorth());
        assertFalse(tile.isExitEast());
        assertFalse(tile.isExitSouth());
        assertTrue(tile.isExitWest());
    }

    @Test
    public void testMultipleExitsRotateCounterClockwise() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(true);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateCounterClockwise();

        assertFalse(tile.isExitNorth());
        assertTrue(tile.isExitEast());
        assertTrue(tile.isExitSouth());
        assertFalse(tile.isExitWest());
    }

    @Test
    public void testFourRotationsClockwiseReturnsToOriginal() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(false);
        tile.setExitSouth(true);
        tile.setExitWest(false);

        tile.rotateClockwise(4);

        assertTrue(tile.isExitNorth());
        assertFalse(tile.isExitEast());
        assertTrue(tile.isExitSouth());
        assertFalse(tile.isExitWest());
    }

    @Test
    public void testFourRotationsCounterClockwiseReturnsToOriginal() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(false);
        tile.setExitSouth(true);
        tile.setExitWest(false);

        tile.rotateCounterClockwise(4);

        assertTrue(tile.isExitNorth());
        assertFalse(tile.isExitEast());
        assertTrue(tile.isExitSouth());
        assertFalse(tile.isExitWest());
    }

    @Test
    public void testAllExitsStayAllExits() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(true);
        tile.setExitEast(true);
        tile.setExitSouth(true);
        tile.setExitWest(true);

        tile.rotateClockwise();

        assertTrue(tile.isExitNorth());
        assertTrue(tile.isExitEast());
        assertTrue(tile.isExitSouth());
        assertTrue(tile.isExitWest());

        tile.rotateCounterClockwise();

        assertTrue(tile.isExitNorth());
        assertTrue(tile.isExitEast());
        assertTrue(tile.isExitSouth());
        assertTrue(tile.isExitWest());
    }

    @Test
    public void testNoExitsStayNoExits() {
        BoardTile tile = new BoardTile();
        tile.setExitNorth(false);
        tile.setExitEast(false);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateClockwise();

        assertFalse(tile.isExitNorth());
        assertFalse(tile.isExitEast());
        assertFalse(tile.isExitSouth());
        assertFalse(tile.isExitWest());
    }

    @Test
    public void testRotationAngleUpdatesClockwise() {
        BoardTile tile = new BoardTile();
        tile.setRotation(0);
        tile.setExitNorth(true);
        tile.setExitEast(false);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateClockwise();
        assertEquals(270, tile.getRotation());

        tile.rotateClockwise();
        assertEquals(180, tile.getRotation());

        tile.rotateClockwise();
        assertEquals(90, tile.getRotation());

        tile.rotateClockwise();
        assertEquals(0, tile.getRotation());
    }

    @Test
    public void testRotationAngleUpdatesCounterClockwise() {
        BoardTile tile = new BoardTile();
        tile.setRotation(0);
        tile.setExitNorth(true);
        tile.setExitEast(false);
        tile.setExitSouth(false);
        tile.setExitWest(false);

        tile.rotateCounterClockwise();
        assertEquals(90, tile.getRotation());

        tile.rotateCounterClockwise();
        assertEquals(180, tile.getRotation());

        tile.rotateCounterClockwise();
        assertEquals(270, tile.getRotation());

        tile.rotateCounterClockwise();
        assertEquals(0, tile.getRotation());
    }
}
