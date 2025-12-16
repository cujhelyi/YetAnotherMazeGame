package com.example.labyrinth;

import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.Player;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.repository.PlayerRepository;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PlayerInitializationTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testBoardCreationInitializesFourPlayers() throws Exception {
        GameBoard board = boardService.createBoardFromJson("tiles/board-50.json");
        
        assertNotNull(board.getPlayersList());
        assertEquals(4, board.getPlayersList().size());
    }

    @Test
    public void testPlayersStartInCorners() throws Exception {
        GameBoard board = boardService.createBoardFromJson("tiles/board-50.json");
        
        // Player 1 should be at (0, 0)
        Player player1 = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(player1);
        assertEquals(0, player1.getRowIndex());
        assertEquals(0, player1.getColIndex());

        // Player 2 should be at (0, 6)
        Player player2 = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull(player2);
        assertEquals(0, player2.getRowIndex());
        assertEquals(6, player2.getColIndex());

        // Player 3 should be at (6, 0)
        Player player3 = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(player3);
        assertEquals(6, player3.getRowIndex());
        assertEquals(0, player3.getColIndex());

        // Player 4 should be at (6, 6)
        Player player4 = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 4)
                .findFirst()
                .orElse(null);
        assertNotNull(player4);
        assertEquals(6, player4.getRowIndex());
        assertEquals(6, player4.getColIndex());
    }

    @Test
    public void testPlayerNumbersAreUnique() throws Exception {
        GameBoard board = boardService.createBoardFromJson("tiles/board-50.json");
        
        long uniquePlayerNumbers = board.getPlayersList().stream()
                .map(Player::getPlayerNumber)
                .distinct()
                .count();
        
        assertEquals(4, uniquePlayerNumbers);
    }

    @Test
    public void testPlayersHaveCorrectGameBoardReference() throws Exception {
        GameBoard board = boardService.createBoardFromJson("tiles/board-50.json");
        
        for (Player player : board.getPlayersList()) {
            assertNotNull(player.getGameBoard());
            assertEquals(board.getId(), player.getGameBoard().getId());
        }
    }

    @Test
    public void testPlayersPersistWithBoard() throws Exception {
        GameBoard board = boardService.createBoardFromJson("tiles/board-50.json");
        Long boardId = board.getId();
        
        // Reload board from database
        GameBoard reloadedBoard = boardService.loadBoard(boardId);
        
        assertNotNull(reloadedBoard.getPlayersList());
        assertEquals(4, reloadedBoard.getPlayersList().size());
    }
}
