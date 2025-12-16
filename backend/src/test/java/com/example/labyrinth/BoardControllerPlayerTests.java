package com.example.labyrinth;

import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.Player;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.repository.PlayerRepository;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BoardControllerPlayerTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private GameBoard board;

    @BeforeEach
    public void setup() throws Exception {
        board = boardService.createBoardFromJson("tiles/board-50.json");
    }

    @Test
    public void testGetBoardIncludesPlayers() throws Exception {
        GameBoard loadedBoard = boardService.loadBoard(board.getId());
        
        assertNotNull(loadedBoard);
        assertNotNull(loadedBoard.getPlayersList());
        assertEquals(4, loadedBoard.getPlayersList().size());
    }

    @Test
    public void testMovePlayer() throws Exception {
        Player player = board.getPlayersList().get(0);
        Long playerId = player.getId();
        
        board = boardService.movePlayer(board.getId(), playerId, 3, 3);
        
        Player movedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElse(null);
        
        assertNotNull(movedPlayer);
        assertEquals(3, movedPlayer.getRowIndex());
        assertEquals(3, movedPlayer.getColIndex());
    }

    @Test
    public void testInsertSpareWithPlayers() throws Exception {
        assertEquals(4, board.getPlayersList().size());
        
        board = boardService.insertSpare(board.getId(), 1, null, "start");
        
        assertNotNull(board.getPlayersList());
        assertEquals(4, board.getPlayersList().size());
    }

    @Test
    public void testAllFourPlayersPresent() throws Exception {
        GameBoard loadedBoard = boardService.loadBoard(board.getId());
        
        assertEquals(4, loadedBoard.getPlayersList().size());
        
        for (int i = 1; i <= 4; i++) {
            final int playerNum = i;
            Player player = loadedBoard.getPlayersList().stream()
                    .filter(p -> p.getPlayerNumber() == playerNum)
                    .findFirst()
                    .orElse(null);
            assertNotNull(player, "Player " + i + " should exist");
        }
    }
}
