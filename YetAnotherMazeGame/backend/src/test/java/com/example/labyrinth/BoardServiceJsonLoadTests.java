package com.example.labyrinth;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardServiceJsonLoadTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Test
    @Transactional
    public void createBoardFromJsonAndPersist() throws Exception {
        GameBoard gb = boardService.createBoardFromJson("tiles/board-50.json");
        assertNotNull(gb.getId(), "saved board should have id");

        GameBoard reloaded = gameBoardRepository.findById(gb.getId()).orElse(null);
        assertNotNull(reloaded);

        // populate transient state (within transaction so lazy collection can be initialized)
        reloaded.populateFromTiles();

        assertEquals(50, reloaded.getTiles().size(), "should have 50 saved tiles (including spare)");

        long spareCount = reloaded.getTiles().stream().filter(t -> t.getRowIndex() < 0 || t.getColIndex() < 0).count();
        assertEquals(1, spareCount, "exactly one spare tile expected");

        // count non-null board cells
        BoardTile[][] arr = reloaded.getBoardArray();
        int filled = 0;
        for (int r = 0; r < reloaded.getBoardSize(); r++) {
            for (int c = 0; c < reloaded.getBoardSize(); c++) {
                if (arr[r][c] != null) filled++;
            }
        }
        assertEquals(49, filled, "49 board cells should be filled");
    }
}
