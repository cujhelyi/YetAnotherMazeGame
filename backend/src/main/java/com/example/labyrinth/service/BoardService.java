package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.repository.BoardTileRepository;
import com.example.labyrinth.repository.GameBoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.util.Map;

@Service
public class BoardService {

    private final GameBoardRepository gameBoardRepository;
    private final BoardTileRepository boardTileRepository;

    public BoardService(GameBoardRepository gameBoardRepository,
                        BoardTileRepository boardTileRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.boardTileRepository = boardTileRepository;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Transactional
    public GameBoard createBoardFromJson(String resourcePath) throws Exception {
        GameBoard gb = new GameBoard();
        gb.resetBoardArray();

        List<Map<String, Object>> items = objectMapper.readValue(
                new ClassPathResource(resourcePath).getInputStream(),
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});

        for (Map<String, Object> m : items) {
            BoardTile t = new BoardTile();
            if (Boolean.TRUE.equals(m.get("spare"))) {
                t.setRowIndex(-1);
                t.setColIndex(-1);
            } else {
                Number row = (Number) m.get("row");
                Number col = (Number) m.get("col");
                t.setRowIndex(row.intValue());
                t.setColIndex(col.intValue());
            }
            t.setExitNorth(Boolean.TRUE.equals(m.get("exitNorth")));
            t.setExitEast(Boolean.TRUE.equals(m.get("exitEast")));
            t.setExitSouth(Boolean.TRUE.equals(m.get("exitSouth")));
            t.setExitWest(Boolean.TRUE.equals(m.get("exitWest")));
            t.setTreasure((String) m.getOrDefault("treasure", ""));
            t.setImage((String) m.get("image"));
            t.setLocked(Boolean.TRUE.equals(m.get("locked")));
            gb.addTile(t);
            if (t.getRowIndex() < 0) gb.setSparePiece(t);
        }

        GameBoard saved = gameBoardRepository.save(gb);
        // populate transient state from persisted tiles
        saved.populateFromTiles();
        return saved;
    }

    @Transactional
    public BoardTile rotateSpareClockwise(Long boardId, int times) {
        GameBoard gb = gameBoardRepository.findById(boardId).orElse(null);
        if (gb == null) return null;
        // ensure transient state is populated
        gb.populateFromTiles();
        BoardTile spare = gb.getSparePiece();
        if (spare == null) throw new IllegalStateException("Board has no spare tile to rotate");
        spare.rotateClockwise(times);
        // persist tile change
        return boardTileRepository.save(spare);
    }

    @Transactional
    public BoardTile rotateSpareCounterclockwise(Long boardId, int times) {
        GameBoard gb = gameBoardRepository.findById(boardId).orElse(null);
        if (gb == null) return null;
        // ensure transient state is populated
        gb.populateFromTiles();
        BoardTile spare = gb.getSparePiece();
        if (spare == null) throw new IllegalStateException("Board has no spare tile to rotate");
        spare.rotateCounterClockwise(times);
        // persist tile change
        return boardTileRepository.save(spare);
    }

    /**
     * Rotate the globally unique spare tile clockwise by the given number of times.
     * Assumes a single spare tile exists with rowIndex = -1 and colIndex = -1.
     */
    @Transactional
    public BoardTile rotateSpareClockwise(int times) {
        BoardTile spare = boardTileRepository.findFirstByRowIndexAndColIndex(-1, -1)
                .orElseThrow(() -> new IllegalStateException("No spare tile found"));
        spare.rotateClockwise(times);
        return boardTileRepository.save(spare);
    }

    /**
     * Rotate the globally unique spare tile counterclockwise by the given number of times.
     * Assumes a single spare tile exists with rowIndex = -1 and colIndex = -1.
     */
    @Transactional
    public BoardTile rotateSpareCounterclockwise(int times) {
        BoardTile spare = boardTileRepository.findFirstByRowIndexAndColIndex(-1, -1)
                .orElseThrow(() -> new IllegalStateException("No spare tile found"));
        spare.rotateCounterClockwise(times);
        return boardTileRepository.save(spare);
    }

    @Transactional
    public GameBoard shuffleBoard(Long boardId) {
        return shuffleBoard(boardId, null);
    }

    @Transactional
    public GameBoard shuffleBoard(Long boardId, Long seed) {
        GameBoard gb = gameBoardRepository.findById(boardId).orElse(null);
        if (gb == null) return null;
        gb.populateFromTiles();

        // collect tiles (including spare)
        java.util.List<BoardTile> allTiles = new java.util.ArrayList<>(gb.getTiles());
        if (allTiles.size() != 50) {
            throw new IllegalStateException("Board must have 50 tiles (49 + spare) to shuffle, found=" + allTiles.size());
        }

        // separate locked and unlocked tiles
        java.util.List<BoardTile> lockedTiles = allTiles.stream()
                .filter(BoardTile::isLocked)
                .collect(java.util.stream.Collectors.toList());
        java.util.List<BoardTile> unlocked = allTiles.stream()
                .filter(t -> !t.isLocked())
                .collect(java.util.stream.Collectors.toList());

        java.util.Random rnd = (seed == null) ? new java.util.Random() : new java.util.Random(seed);

        // shuffle only unlocked tiles
        java.util.Collections.shuffle(unlocked, rnd);

        // collect positions occupied by locked tiles
        java.util.Set<String> lockedPositions = new java.util.HashSet<>();
        for (BoardTile lt : lockedTiles) {
            if (lt.getRowIndex() >= 0) {
                lockedPositions.add(lt.getRowIndex() + "," + lt.getColIndex());
            }
        }

        // assign unlocked tiles to free positions row-major, skipping locked positions
        int idx = 0;
        int size = gb.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (lockedPositions.contains(r + "," + c)) {
                    continue; // skip locked position
                }
                if (idx >= unlocked.size()) break;
                BoardTile t = unlocked.get(idx++);
                t.setRowIndex(r);
                t.setColIndex(c);
                // rotate to random orientation
                int rotations = rnd.nextInt(4);
                t.rotateClockwise(rotations);
                t.setGameBoard(gb);
            }
        }

        // remaining unlocked tiles (if any) become spare (typically 1)
        for (int i = idx; i < unlocked.size(); i++) {
            BoardTile spare = unlocked.get(i);
            spare.setRowIndex(-1);
            spare.setColIndex(-1);
            int spareRot = rnd.nextInt(4);
            spare.rotateClockwise(spareRot);
            spare.setGameBoard(gb);
        }

        // persist all modified tiles
        boardTileRepository.saveAll(allTiles);

        // ensure game board transient state matches persisted tiles
        gb.populateFromTiles();
        return gb;
    }
}
