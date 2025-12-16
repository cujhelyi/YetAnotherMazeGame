package com.example.labyrinth.controller;

import com.example.labyrinth.dto.BoardDTO;
import com.example.labyrinth.dto.TileDTO;
import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "http://localhost:5173")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard() {
        GameBoard gb = boardService.createDefaultBoard();
        BoardDTO dto = toDTO(gb);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(gb.getId()).toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long id) {
        GameBoard gb = boardService.loadBoard(id);
        if (gb == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(gb));
    }

    private BoardDTO toDTO(GameBoard gb) {
        BoardDTO dto = new BoardDTO();
        dto.setId(gb.getId());
        dto.setSize(gb.getBoardSize());
        List<TileDTO> tiles = gb.getTiles().stream().map(this::toTileDTO).collect(Collectors.toList());
        dto.setTiles(tiles);
        return dto;
    }

    private TileDTO toTileDTO(BoardTile t) {
        TileDTO td = new TileDTO();
        td.setId(t.getId());
        td.setRowIndex(t.getRowIndex());
        td.setColIndex(t.getColIndex());
        td.setExitNorth(t.isExitNorth());
        td.setExitEast(t.isExitEast());
        td.setExitSouth(t.isExitSouth());
        td.setExitWest(t.isExitWest());
        td.setTreasure(t.getTreasure());
        return td;
    }
}
