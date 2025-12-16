package com.example.labyrinth.service;

import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.repository.BoardTileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TileRotationService {

    private final BoardTileRepository boardTileRepository;

    public TileRotationService(BoardTileRepository boardTileRepository) {
        this.boardTileRepository = boardTileRepository;
    }

    @Transactional
    public BoardTile rotateSpareClockwise(int times) {
        BoardTile spare = findSpareTile();
        spare.rotateClockwise(times);
        return boardTileRepository.save(spare);
    }

    @Transactional
    public BoardTile rotateSpareCounterclockwise(int times) {
        BoardTile spare = findSpareTile();
        spare.rotateCounterClockwise(times);
        return boardTileRepository.save(spare);
    }

    private BoardTile findSpareTile() {
        return boardTileRepository.findFirstByRowIndexAndColIndex(-1, -1)
                .orElseThrow(() -> new IllegalStateException("No spare tile found"));
    }
}
