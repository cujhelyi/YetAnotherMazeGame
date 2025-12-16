package com.example.labyrinth;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.Player;
import com.example.labyrinth.repository.BoardTileRepository;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.repository.PlayerRepository;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BoardServiceInsertTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Autowired
    private BoardTileRepository boardTileRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private GameBoard board;

    @BeforeEach
    public void setup() throws Exception {
        board = boardService.createBoardFromJson("tiles/board-50.json");
    }

    @Test
    public void testInsertIntoRowStart() {
        BoardTile spare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(spare);
        String spareImage = spare.getImage();
        
        BoardTile originalEnd = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 1 && t.getColIndex() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(originalEnd);
        String endImage = originalEnd.getImage();

        board = boardService.insertSpare(board.getId(), 1, null, "start");

        BoardTile newTileAtStart = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 1 && t.getColIndex() == 0)
                .findFirst()
                .orElse(null);
        assertNotNull(newTileAtStart);
        assertEquals(spareImage, newTileAtStart.getImage());

        // Verify old end tile is now spare
        BoardTile newSpare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(newSpare);
        assertEquals(endImage, newSpare.getImage());
    }

    @Test
    public void testInsertIntoRowEnd() {
        BoardTile spare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(spare);
        String spareImage = spare.getImage();

        // Get tile that will be pushed out
        BoardTile originalStart = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 3 && t.getColIndex() == 0)
                .findFirst()
                .orElse(null);
        assertNotNull(originalStart);
        String startImage = originalStart.getImage();

        // Insert spare into row 3 from end
        board = boardService.insertSpare(board.getId(), 3, null, "end");

        // Verify spare is now at position (3, 6)
        BoardTile newTileAtEnd = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 3 && t.getColIndex() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(newTileAtEnd);
        assertEquals(spareImage, newTileAtEnd.getImage());

        // Verify old start tile is now spare
        BoardTile newSpare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(newSpare);
        assertEquals(startImage, newSpare.getImage());
    }

    @Test
    public void testInsertIntoColumnStart() {
        BoardTile spare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(spare);
        String spareImage = spare.getImage();

        // Get tile that will be pushed out
        BoardTile originalEnd = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 6 && t.getColIndex() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(originalEnd);
        String endImage = originalEnd.getImage();

        // Insert spare into column 1 from start
        board = boardService.insertSpare(board.getId(), null, 1, "start");

        // Verify spare is now at position (0, 1)
        BoardTile newTileAtStart = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 0 && t.getColIndex() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(newTileAtStart);
        assertEquals(spareImage, newTileAtStart.getImage());

        // Verify old end tile is now spare
        BoardTile newSpare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(newSpare);
        assertEquals(endImage, newSpare.getImage());
    }

    @Test
    public void testInsertIntoColumnEnd() {
        BoardTile spare = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == -1 && t.getColIndex() == -1)
                .findFirst()
                .orElse(null);
        assertNotNull(spare);
        String spareImage = spare.getImage();

        board = boardService.insertSpare(board.getId(), null, 5, "end");

        BoardTile newTileAtEnd = board.getTiles().stream()
                .filter(t -> t.getRowIndex() == 6 && t.getColIndex() == 5)
                .findFirst()
                .orElse(null);
        assertNotNull(newTileAtEnd);
        assertEquals(spareImage, newTileAtEnd.getImage());
    }

    @Test
    public void testInsertInvalidRowColumn() {
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.insertSpare(board.getId(), 0, null, "start");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            boardService.insertSpare(board.getId(), null, 2, "start");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            boardService.insertSpare(board.getId(), 1, 1, "start");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            boardService.insertSpare(board.getId(), null, null, "start");
        });
    }

    @Test
    public void testPlayerMovesWithRowInsertion() {
        Player player = new Player();
        player.setPlayerNumber(5);
        player.setRowIndex(1);
        player.setColIndex(0);
        player.setGameBoard(board);
        board.addPlayer(player);
        playerRepository.save(player);
        gameBoardRepository.save(board);

        board = boardService.insertSpare(board.getId(), 1, null, "start");

        Player movedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 5)
                .findFirst()
                .orElse(null);
        assertNotNull(movedPlayer);
        assertEquals(1, movedPlayer.getRowIndex());
        assertEquals(1, movedPlayer.getColIndex());
    }

    @Test
    public void testPlayerWrapsWhenPushedOut() {
        Player player = new Player();
        player.setPlayerNumber(5);
        player.setRowIndex(1);
        player.setColIndex(6);
        player.setGameBoard(board);
        board.addPlayer(player);
        playerRepository.save(player);
        gameBoardRepository.save(board);

        board = boardService.insertSpare(board.getId(), 1, null, "start");

        Player movedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 5)
                .findFirst()
                .orElse(null);
        assertNotNull(movedPlayer);
        assertEquals(1, movedPlayer.getRowIndex());
        assertEquals(0, movedPlayer.getColIndex(), "Player should wrap to insertion point");
    }

    @Test
    public void testMultiplePlayersMove() {
        Player player1 = new Player();
        player1.setPlayerNumber(5);
        player1.setRowIndex(3);
        player1.setColIndex(2);
        player1.setGameBoard(board);
        board.addPlayer(player1);

        Player player2 = new Player();
        player2.setPlayerNumber(6);
        player2.setRowIndex(3);
        player2.setColIndex(5);
        player2.setGameBoard(board);
        board.addPlayer(player2);

        playerRepository.save(player1);
        playerRepository.save(player2);
        gameBoardRepository.save(board);

        board = boardService.insertSpare(board.getId(), 3, null, "start");

        List<Player> players = board.getPlayersList();
        assertEquals(6, players.size(), "Should have 4 corner players + 2 test players");

        Player movedPlayer1 = players.stream()
                .filter(p -> p.getPlayerNumber() == 5)
                .findFirst()
                .orElse(null);
        assertNotNull(movedPlayer1);
        assertEquals(3, movedPlayer1.getRowIndex());
        assertEquals(3, movedPlayer1.getColIndex());

        Player movedPlayer2 = players.stream()
                .filter(p -> p.getPlayerNumber() == 6)
                .findFirst()
                .orElse(null);
        assertNotNull(movedPlayer2);
        assertEquals(3, movedPlayer2.getRowIndex());
        assertEquals(6, movedPlayer2.getColIndex());
    }

    @Test
    public void testPlayerMovesWithColumnInsertion() {
        Player player = new Player();
        player.setPlayerNumber(5);
        player.setRowIndex(0);
        player.setColIndex(5);
        player.setGameBoard(board);
        board.addPlayer(player);
        playerRepository.save(player);
        gameBoardRepository.save(board);

        board = boardService.insertSpare(board.getId(), null, 5, "start");

        Player movedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 5)
                .findFirst()
                .orElse(null);
        assertNotNull(movedPlayer);
        assertEquals(1, movedPlayer.getRowIndex());
        assertEquals(5, movedPlayer.getColIndex());
    }

    @Test
    public void testPlayerWrapsInColumn() {
        Player player = new Player();
        player.setPlayerNumber(5);
        player.setRowIndex(6);
        player.setColIndex(3);
        player.setGameBoard(board);
        board.addPlayer(player);
        playerRepository.save(player);
        gameBoardRepository.save(board);

        board = boardService.insertSpare(board.getId(), null, 3, "start");

        Player movedPlayer = board.getPlayersList().stream()
                .filter(p -> p.getPlayerNumber() == 5)
                .findFirst()
                .orElse(null);
        assertNotNull(movedPlayer);
        assertEquals(0, movedPlayer.getRowIndex(), "Player should wrap to insertion point");
        assertEquals(3, movedPlayer.getColIndex());
    }
}
