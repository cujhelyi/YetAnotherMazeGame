package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.repository.BoardTileRepository;
import com.example.labyrinth.repository.GameBoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class BoardShuffleService {

    private final GameBoardRepository gameBoardRepository;
    private final BoardTileRepository boardTileRepository;

    public BoardShuffleService(GameBoardRepository gameBoardRepository,
                               BoardTileRepository boardTileRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.boardTileRepository = boardTileRepository;
    }

    @Transactional
    public GameBoard shuffleBoard(Long boardId) {
        return shuffleBoard(boardId, null);
    }

    @Transactional
    public GameBoard shuffleBoard(Long boardId, Long seed) {
        GameBoard gameBoard = gameBoardRepository.findById(boardId).orElse(null);
        if (gameBoard == null) return null;
        gameBoard.populateFromTiles();

        List<BoardTile> allTiles = findAllGameTiles(gameBoard);

        List<BoardTile> lockedTiles = allTiles.stream()
                .filter(BoardTile::isLocked)
                .collect(java.util.stream.Collectors.toList());
        List<BoardTile> unlocked = allTiles.stream()
                .filter(t -> !t.isLocked())
                .collect(java.util.stream.Collectors.toList());

        Random rnd = (seed == null) ? new Random() : new Random(seed);

        java.util.Collections.shuffle(unlocked, rnd);

        java.util.Set<String> lockedPositions = collectLockedPositions(lockedTiles);

        int size = gameBoard.getBoardSize();
        int idx = 0;
        idx = shuffleUnlockedTiles(size, idx, unlocked, lockedPositions, rnd);
        
        setSpareTile(unlocked, idx, gameBoard);

        boardTileRepository.saveAll(allTiles);

        gameBoard.populateFromTiles();
        return gameBoard;
    }

    private List<BoardTile> findAllGameTiles(GameBoard gameBoard) {
        List<BoardTile> allTiles = new java.util.ArrayList<>(gameBoard.getTiles());
        if (allTiles.size() != 50) {
            throw new IllegalStateException("Board must have 50 tiles (49 + spare) to shuffle, found=" + allTiles.size());
        }
        return allTiles;
    }

    private java.util.Set<String> collectLockedPositions(List<BoardTile> lockedTiles) {
        java.util.Set<String> positions = new java.util.HashSet<>();
        for (BoardTile t : lockedTiles) {
            positions.add(t.getRowIndex() + "," + t.getColIndex());
        }
        return positions;
    }

    private int shuffleUnlockedTiles(int size,
                                      int idx,
                                      List<BoardTile> unlocked,
                                      java.util.Set<String> lockedPositions,
                                      Random rnd) {
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (lockedPositions.contains(row + "," + col)) {
                    continue;
                }
                if (idx >= unlocked.size()) break;
                BoardTile tile = unlocked.get(idx++);
                tile.setRowIndex(row);
                tile.setColIndex(col);

                rotateToRandomOrientation(tile, rnd);
            }
        }
        return idx;
    }

    private void setSpareTile(List<BoardTile> unlocked, int idx, GameBoard gameBoard) {
        if (idx >= unlocked.size()) {
            throw new IllegalStateException("No tile available to set as spare");
        }
        BoardTile spare = unlocked.get(idx);
        spare.setRowIndex(-1);
        spare.setColIndex(-1);
        rotateToRandomOrientation(spare, new Random());
        gameBoard.setSparePiece(spare);
    }

    private void rotateToRandomOrientation(BoardTile tile, Random rnd) {
        int rotations = rnd.nextInt(4);
        tile.rotateClockwise(rotations);
    }
}
