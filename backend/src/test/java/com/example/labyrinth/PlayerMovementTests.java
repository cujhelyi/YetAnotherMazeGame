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
public class PlayerMovementTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private GameBoard board;
    private Player player;

    @BeforeEach
    public void setup() throws Exception {
        board = boardService.createBoardFromJson("tiles/board-50.json");
        
        player = new Player();
        player.setPlayerNumber(5);
        player.setRowIndex(0);
        player.setColIndex(0);
        player.setGameBoard(board);
        board.addPlayer(player);
        playerRepository.save(player);
        gameBoardRepository.save(board);
    }

    @Test
    public void testMovePlayerToValidPosition() {
        board = boardService.movePlayer(board.getId(), player.getId(), 3, 3);
        
        Player movedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getId().equals(player.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(movedPlayer);
        assertEquals(3, movedPlayer.getRowIndex());
        assertEquals(3, movedPlayer.getColIndex());
    }

    @Test
    public void testMovePlayerWithinBounds() {
        board = boardService.movePlayer(board.getId(), player.getId(), 0, 6);
        Player p = board.getPlayersList().stream()
                .filter(player -> player.getId().equals(this.player.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(p);
        assertEquals(0, p.getRowIndex());
        assertEquals(6, p.getColIndex());

        board = boardService.movePlayer(board.getId(), player.getId(), 6, 0);
        p = board.getPlayersList().stream()
                .filter(player -> player.getId().equals(this.player.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(p);
        assertEquals(6, p.getRowIndex());
        assertEquals(0, p.getColIndex());

        board = boardService.movePlayer(board.getId(), player.getId(), 6, 6);
        p = board.getPlayersList().stream()
                .filter(player -> player.getId().equals(this.player.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(p);
        assertEquals(6, p.getRowIndex());
        assertEquals(6, p.getColIndex());
    }

    @Test
    public void testMovePlayerOutOfBoundsThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.movePlayer(board.getId(), player.getId(), -1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            boardService.movePlayer(board.getId(), player.getId(), 0, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            boardService.movePlayer(board.getId(), player.getId(), 7, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            boardService.movePlayer(board.getId(), player.getId(), 0, 7);
        });
    }

    @Test
    public void testMoveNonexistentPlayerThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.movePlayer(board.getId(), 99999L, 3, 3);
        });
    }

    @Test
    public void testMultiplePlayersCanOccupySameTile() {
        Player player2 = new Player();
        player2.setPlayerNumber(2);
        player2.setRowIndex(1);
        player2.setColIndex(1);
        player2.setGameBoard(board);
        board.addPlayer(player2);
        playerRepository.save(player2);
        gameBoardRepository.save(board);

        board = boardService.movePlayer(board.getId(), player.getId(), 3, 3);
        board = boardService.movePlayer(board.getId(), player2.getId(), 3, 3);

        long playersAt33 = board.getPlayersList().stream()
                .filter(p -> p.getRowIndex() == 3 && p.getColIndex() == 3)
                .count();
        
        assertEquals(2, playersAt33);
    }

    @Test
    public void testPlayerPersistsAfterMove() {
        board = boardService.movePlayer(board.getId(), player.getId(), 5, 5);
        
        // Reload from database
        board = boardService.loadBoard(board.getId());
        
        Player reloadedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getId().equals(player.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(reloadedPlayer);
        assertEquals(5, reloadedPlayer.getRowIndex());
        assertEquals(5, reloadedPlayer.getColIndex());
    }
}
