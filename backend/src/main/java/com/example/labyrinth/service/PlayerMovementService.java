package com.example.labyrinth.service;

import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.Player;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerMovementService {

    private final GameBoardRepository gameBoardRepository;
    private final PlayerRepository playerRepository;

    public PlayerMovementService(GameBoardRepository gameBoardRepository,
                                 PlayerRepository playerRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public GameBoard movePlayer(Long boardId, Long playerId, int row, int col) {
        GameBoard gameBoard = gameBoardRepository.findById(boardId).orElse(null);
        if (gameBoard == null) return null;
        
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null) throw new IllegalArgumentException("Player not found");
        
        int size = gameBoard.getBoardSize();
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IllegalArgumentException("Target position out of bounds");
        }
        
        player.setRowIndex(row);
        player.setColIndex(col);
        playerRepository.save(player);
        
        gameBoard.populateFromTiles();
        return gameBoard;
    }
}
