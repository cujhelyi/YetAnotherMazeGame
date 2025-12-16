package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import org.springframework.stereotype.Service;

/**
 * Facade service that delegates to specialized board operation services.
 * This maintains backward compatibility with controllers while organizing
 * functionality into focused, single-responsibility services.
 */
@Service
public class BoardService {

    private final BoardCreationService boardCreationService;
    private final TileRotationService tileRotationService;
    private final BoardShuffleService boardShuffleService;
    private final TileInsertionService tileInsertionService;
    private final PlayerMovementService playerMovementService;

    public BoardService(BoardCreationService boardCreationService,
                        TileRotationService tileRotationService,
                        BoardShuffleService boardShuffleService,
                        TileInsertionService tileInsertionService,
                        PlayerMovementService playerMovementService) {
        this.boardCreationService = boardCreationService;
        this.tileRotationService = tileRotationService;
        this.boardShuffleService = boardShuffleService;
        this.tileInsertionService = tileInsertionService;
        this.playerMovementService = playerMovementService;
    }

    public GameBoard createDefaultBoard() {
        return boardCreationService.createDefaultBoard();
    }

    public GameBoard loadBoard(Long id) {
        return boardCreationService.loadBoard(id);
    }

    public GameBoard createBoardFromJson(String resourcePath) throws Exception {
        return boardCreationService.createBoardFromJson(resourcePath);
    }

    public BoardTile rotateSpareClockwise(int times) {
        return tileRotationService.rotateSpareClockwise(times);
    }

    public BoardTile rotateSpareCounterclockwise(int times) {
        return tileRotationService.rotateSpareCounterclockwise(times);
    }

    public GameBoard shuffleBoard(Long boardId) {
        return boardShuffleService.shuffleBoard(boardId);
    }

    public GameBoard shuffleBoard(Long boardId, Long seed) {
        return boardShuffleService.shuffleBoard(boardId, seed);
    }

    public GameBoard insertSpare(Long boardId, Integer row, Integer col, String position) {
        return tileInsertionService.insertSpare(boardId, row, col, position);
    }

    public GameBoard movePlayer(Long boardId, Long playerId, int row, int col) {
        return playerMovementService.movePlayer(boardId, playerId, row, col);
    }
}
