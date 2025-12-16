package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.Player;
import com.example.labyrinth.repository.GameBoardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BoardCreationService {

    private final GameBoardRepository gameBoardRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BoardCreationService(GameBoardRepository gameBoardRepository) {
        this.gameBoardRepository = gameBoardRepository;
    }

    @Transactional
    public GameBoard createDefaultBoard() {
        try {
            return createBoardFromJson("tiles/board-50.json");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default board from JSON", e);
        }
    }

    @Transactional(readOnly = true)
    public GameBoard loadBoard(Long id) {
        return gameBoardRepository.findById(id).orElse(null);
    }

    @Transactional
    public GameBoard createBoardFromJson(String resourcePath) throws Exception {
        GameBoard gameBoard = new GameBoard();
        gameBoard.resetBoardArray();

        List<Map<String, Object>> items = objectMapper.readValue(
                new ClassPathResource(resourcePath).getInputStream(),
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});

        for (Map<String, Object> m : items) {
            BoardTile tile = new BoardTile();
            if (Boolean.TRUE.equals(m.get("spare"))) {
                tile.setRowIndex(-1);
                tile.setColIndex(-1);
            } else {
                Number row = (Number) m.get("row");
                Number col = (Number) m.get("col");
                tile.setRowIndex(row.intValue());
                tile.setColIndex(col.intValue());
            }
            tile.setExitNorth(Boolean.TRUE.equals(m.get("exitNorth")));
            tile.setExitEast(Boolean.TRUE.equals(m.get("exitEast")));
            tile.setExitSouth(Boolean.TRUE.equals(m.get("exitSouth")));
            tile.setExitWest(Boolean.TRUE.equals(m.get("exitWest")));
            tile.setTreasure((String) m.getOrDefault("treasure", ""));
            tile.setImage((String) m.get("image"));
            tile.setLocked(Boolean.TRUE.equals(m.get("locked")));
            
            gameBoard.addTile(tile);
            if (tile.getRowIndex() < 0) gameBoard.setSparePiece(tile);
        }

        GameBoard saved = gameBoardRepository.save(gameBoard);
        
        int size = saved.getBoardSize();
        int[] topRightCorner = {0, 0};
        int[] topLeftCorner = {0, size - 1};
        int[] bottomRightCorner = {size - 1, 0};
        int[] bottomLeftCorner = {size - 1, size - 1};

        Player player1 = new Player(1, topRightCorner[0], topRightCorner[1]);
        Player player2 = new Player(2, topLeftCorner[0], topLeftCorner[1]);
        Player player3 = new Player(3, bottomRightCorner[0], bottomRightCorner[1]);
        Player player4 = new Player(4, bottomLeftCorner[0], bottomLeftCorner[1]);
        
        saved.addPlayer(player1);
        saved.addPlayer(player2);
        saved.addPlayer(player3);
        saved.addPlayer(player4);
        
        gameBoardRepository.save(saved);
        
        saved.populateFromTiles();
        return saved;
    }
}
