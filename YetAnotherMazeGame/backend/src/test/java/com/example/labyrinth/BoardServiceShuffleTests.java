package com.example.labyrinth;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardServiceShuffleTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Test
    @Transactional
    public void shuffleBoard_changesPositionsAndRotations_deterministicSeed() throws Exception {
        // create a board from JSON seed
        GameBoard gb = boardService.createBoardFromJson("tiles/board-50.json");
        assertNotNull(gb.getId());

        // snapshot original exits for a sample of tiles (id -> exits bitmask)
        List<BoardTile> originalTiles = gb.getTiles();
        assertEquals(50, originalTiles.size());

        // map id to bitmask
        java.util.Map<Long,Integer> originalMap = new java.util.HashMap<>();
        for (BoardTile t : originalTiles) {
            originalMap.put(t.getId(), exitsBitmask(t));
        }

        // shuffle with a fixed seed
        GameBoard shuffled = boardService.shuffleBoard(gb.getId(), 12345L);
        assertNotNull(shuffled);

        // persisted tiles should still be 50
        List<BoardTile> after = shuffled.getTiles();
        assertEquals(50, after.size());

        // Check coordinates uniqueness and spare count
        long spareCount = after.stream().filter(t -> t.getRowIndex() < 0).count();
        assertEquals(1, spareCount);

        long coordCount = after.stream().filter(t -> t.getRowIndex() >= 0).map(t -> t.getRowIndex() + ":" + t.getColIndex()).distinct().count();
        assertEquals(49, coordCount);

        // At least one tile must have different exits than before (rotation occurred)
        boolean anyChanged = after.stream().anyMatch(t -> {
            Integer orig = originalMap.get(t.getId());
            return orig == null || orig.intValue() != exitsBitmask(t);
        });

        assertTrue(anyChanged, "Expected at least one tile's exits to change due to rotation");
    }

    private int exitsBitmask(BoardTile t) {
        int mask = 0;
        if (t.isExitNorth()) mask |= 1;
        if (t.isExitEast()) mask |= 2;
        if (t.isExitSouth()) mask |= 4;
        if (t.isExitWest()) mask |= 8;
        return mask;
    }
}
