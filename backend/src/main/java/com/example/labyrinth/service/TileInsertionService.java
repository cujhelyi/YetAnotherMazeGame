package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.Player;
import com.example.labyrinth.repository.BoardTileRepository;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TileInsertionService {

    private final GameBoardRepository gameBoardRepository;
    private final BoardTileRepository boardTileRepository;
    private final PlayerRepository playerRepository;

    public TileInsertionService(GameBoardRepository gameBoardRepository,
                                BoardTileRepository boardTileRepository,
                                PlayerRepository playerRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.boardTileRepository = boardTileRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public GameBoard insertSpare(Long boardId, Integer row, Integer col, String position) {
        GameBoard gameBoard = gameBoardRepository.findById(boardId).orElse(null);
        if (gameBoard == null) return null;
        gameBoard.populateFromTiles();
        
        BoardTile spare = gameBoard.getSparePiece();
        if (spare == null) throw new IllegalStateException("No spare tile found");
        
        int size = gameBoard.getBoardSize();
        
        insertIntoRowOrColumn(gameBoard, spare, row, col, position, size);
        
        boardTileRepository.saveAll(gameBoard.getTiles());
        playerRepository.saveAll(gameBoard.getPlayersList());
        gameBoard.populateFromTiles();
        return gameBoard;
    }

    private void insertIntoRowOrColumn(GameBoard gameBoard, BoardTile spare, Integer row, Integer col, String position, int size) {
        if (row != null && (row < 0 || row >= size || row % 2 == 0)) {
            throw new IllegalArgumentException("Invalid row for insertion: " + row);
        }
        if (col != null && (col < 0 || col >= size || col % 2 == 0)) {
            throw new IllegalArgumentException("Invalid column for insertion: " + col);
        }
        if (row != null && col != null) {
            throw new IllegalArgumentException("Specify either row or column for insertion, not both");
        }
        if (row != null) {
            insertIntoRow(gameBoard, spare, row, position, size);
            return;
        } 
        if (col != null) {
            insertIntoColumn(gameBoard, spare, col, position, size);
            return;
        }
        throw new IllegalArgumentException("Either row or column must be specified for insertion");
    }

    private void insertIntoRow(GameBoard gameBoard, BoardTile spare, int row, String position, int size) {
        boolean insertAtStart = "start".equals(position);
        performInsertion(
            gameBoard, spare, size,
            tiles -> tiles.stream()
                .filter(t -> t.getRowIndex() == row && t.getColIndex() == (insertAtStart ? size - 1 : 0))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tile not found at row " + row)),
            (tiles, i) -> {
                BoardTile tile = tiles.stream()
                    .filter(t -> t.getRowIndex() == row && t.getColIndex() == (insertAtStart ? i - 1 : i + 1))
                    .findFirst().orElse(null);
                if (tile != null) {
                    tile.setColIndex(i);
                }
            },
            p -> p.getRowIndex() == row,
            Player::getColIndex,
            Player::setColIndex,
            () -> {
                spare.setRowIndex(row);
                spare.setColIndex(insertAtStart ? 0 : size - 1);
            },
            insertAtStart
        );
    }
    
    private void insertIntoColumn(GameBoard gameBoard, BoardTile spare, int col, String position, int size) {
        boolean insertAtStart = "start".equals(position);
        performInsertion(
            gameBoard, spare, size,
            tiles -> tiles.stream()
                .filter(t -> t.getRowIndex() == (insertAtStart ? size - 1 : 0) && t.getColIndex() == col)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tile not found at col " + col)),
            (tiles, i) -> {
                BoardTile tile = tiles.stream()
                    .filter(t -> t.getRowIndex() == (insertAtStart ? i - 1 : i + 1) && t.getColIndex() == col)
                    .findFirst().orElse(null);
                if (tile != null) {
                    tile.setRowIndex(i);
                }
            },
            p -> p.getColIndex() == col,
            Player::getRowIndex,
            Player::setRowIndex,            
            () -> {
                spare.setRowIndex(insertAtStart ? 0 : size - 1);
                spare.setColIndex(col);
            },
            insertAtStart
        );
    }
    
    private void performInsertion(
            GameBoard gameBoard,
            BoardTile spare,
            int size,
            java.util.function.Function<List<BoardTile>, BoardTile> findPushedOut,
            java.util.function.BiConsumer<List<BoardTile>, Integer> tileToShift,
            java.util.function.Predicate<Player> isPlayerInLine,
            java.util.function.ToIntFunction<Player> getPlayerPosition,
            java.util.function.BiConsumer<Player, Integer> setPlayerPosition,
            Runnable setSparePosition,
            boolean insertAtStart) {
        
        List<BoardTile> tiles = gameBoard.getTiles();
        List<Player> players = gameBoard.getPlayersList();
        
        BoardTile pushedOut = findPushedOut.apply(tiles);
        
        shiftAllTiles(insertAtStart, size, tiles, tileToShift);
        
        shiftPlayerWithTile(players, isPlayerInLine, getPlayerPosition, insertAtStart, size, setPlayerPosition);
        
        setSparePosition.run();
        
        setNewSpareTile(gameBoard, pushedOut);
    }

    private void shiftAllTiles(boolean insertAtStart, int size, List<BoardTile> tiles, 
        java.util.function.BiConsumer<List<BoardTile>, Integer> tileToShift) {
        if (insertAtStart) {
            for (int i = size - 1; i > 0; i--) {
                tileToShift.accept(tiles, i);
            }
            return;
        }
        for (int i = 0; i < size - 1; i++) {
            tileToShift.accept(tiles, i);
        }        
    }
    
    private void shiftPlayerWithTile(
        List<Player> players, 
        java.util.function.Predicate<Player> isPlayerInLine, 
        java.util.function.ToIntFunction<Player> getPlayerPosition,
        boolean insertAtStart,
        int size,
        java.util.function.BiConsumer<Player, Integer> setPlayerPosition) {
        for (Player player : players) {
            if (isPlayerInLine.test(player)) {
                int pos = getPlayerPosition.applyAsInt(player);
                if (pos == (insertAtStart ? size - 1 : 0)) {
                    setPlayerPosition.accept(player, insertAtStart ? 0 : size - 1);
                } else {
                    setPlayerPosition.accept(player, pos + (insertAtStart ? 1 : -1));
                }
            }
        }
    }

    private void setNewSpareTile(GameBoard gameBoard, BoardTile newSpare) {
        newSpare.setRowIndex(-1);
        newSpare.setColIndex(-1);
        gameBoard.setSparePiece(newSpare);
    }
}
