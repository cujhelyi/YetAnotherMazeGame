package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.repository.BoardTileRepository;
import com.example.labyrinth.repository.GameBoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    private final GameBoardRepository gameBoardRepository;
    private final BoardTileRepository boardTileRepository;

    public BoardService(GameBoardRepository gameBoardRepository,
                        BoardTileRepository boardTileRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.boardTileRepository = boardTileRepository;
    }

    @Transactional
    public GameBoard createDefaultBoard() {
        GameBoard gb = new GameBoard();
        gb.resetBoardArray();
        // create tiles with simple default properties
        int size = gb.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                BoardTile t = new BoardTile();
                t.setRowIndex(r);
                t.setColIndex(c);
                t.setExitNorth(r % 2 == 0);
                t.setExitEast(c % 2 == 0);
                t.setExitSouth(r % 2 == 1);
                t.setExitWest((r + c) % 3 == 0);
                t.setTreasure("");
                gb.addTile(t);
            }
        }

        // save the board; cascade will persist tiles
        return gameBoardRepository.save(gb);
    }

    @Transactional(readOnly = true)
    public GameBoard loadBoard(Long id) {
        return gameBoardRepository.findById(id).orElse(null);
    }
}
