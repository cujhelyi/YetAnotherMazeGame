package com.example.labyrinth;

import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.MapPiece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTests {

    @Test
    void defaultBoardInitialization() {
        GameBoard board = new GameBoard();
        board.resetBoardArray();
        board.setDefaults();
        MapPiece[][] arr = board.getBoardArray();
        assertNotNull(arr);
        assertEquals(board.getBoardSize(), arr.length);
        assertEquals(board.getBoardSize(), arr[0].length);
        // ensure every cell is initialized
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                assertNotNull(arr[i][j], "Cell should be initialized");
            }
        }
    }
}
