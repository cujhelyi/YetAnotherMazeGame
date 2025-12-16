package com.example.labyrinth;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameBoardPersistenceTests {

    @Autowired
    private BoardService boardService;

    @Test
    void createAndLoadDefaultBoard() {
        // BoardService creates tiles directly; no prototype repository

        GameBoard gb = boardService.createDefaultBoard();
        assertNotNull(gb.getId(), "Saved GameBoard should have an id");
        int expectedSize = gb.getBoardSize() * gb.getBoardSize();
        assertEquals(expectedSize, gb.getTiles().size(), "Tiles should be created for every cell");

        // check a persisted tile has an id and piece properties
        BoardTile t = gb.getTiles().get(0);
        assertNotNull(t.getId());
        assertNotNull(t.getTreasure());
    }
}
