package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.entity.MapPiece;
import com.example.labyrinth.repository.BoardTileRepository;
import com.example.labyrinth.repository.GameBoardRepository;
import com.example.labyrinth.repository.MapPieceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    private final GameBoardRepository gameBoardRepository;
    private final MapPieceRepository mapPieceRepository;
    private final BoardTileRepository boardTileRepository;

    public BoardService(GameBoardRepository gameBoardRepository,
                        MapPieceRepository mapPieceRepository,
                        BoardTileRepository boardTileRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.mapPieceRepository = mapPieceRepository;
        this.boardTileRepository = boardTileRepository;
    }

    @Transactional
    public GameBoard createDefaultBoard() {
        // ensure prototypes exist
        List<MapPiece> prototypes = mapPieceRepository.findAll();
        if (prototypes.isEmpty()) {
            // create a few simple prototypes
            for (int i = 1; i <= 6; i++) {
                MapPiece p = new MapPiece(i, i % 2 == 0, i % 3 == 0, i % 2 == 1, i % 5 == 0, "");
                mapPieceRepository.save(p);
            }
            prototypes = mapPieceRepository.findAll();
        }

        GameBoard gb = new GameBoard();
        gb.resetBoardArray();
        // create tiles that reference the prototypes
        int size = gb.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                BoardTile t = new BoardTile();
                t.setRowIndex(r);
                t.setColIndex(c);
                MapPiece proto = prototypes.get((r + c) % prototypes.size());
                t.setMapPiece(proto);
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
