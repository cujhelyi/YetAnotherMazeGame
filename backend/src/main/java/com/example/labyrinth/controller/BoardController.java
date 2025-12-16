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

    @PostMapping("/spare/rotate")
    public ResponseEntity<TileDTO> rotateSpare(@RequestParam(defaultValue = "1") int times,
                                               @RequestParam(defaultValue = "cw") String direction) {
        try {
            BoardTile t;
            if ("ccw".equalsIgnoreCase(direction) || "counterclockwise".equalsIgnoreCase(direction)) {
                t = boardService.rotateSpareCounterclockwise(times);
            } else {
                t = boardService.rotateSpareClockwise(times);
            }
            return ResponseEntity.ok(toTileDTO(t));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Shuffle the board tiles and spare. If `seed` is provided, the shuffle is deterministic.
     */
    @PostMapping("/{id}/shuffle")
    public ResponseEntity<BoardDTO> shuffleBoard(@PathVariable Long id, @RequestParam(required = false) Long seed) {
        try {
            GameBoard gb = (seed == null) ? boardService.shuffleBoard(id) : boardService.shuffleBoard(id, seed);
            if (gb == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(gb));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
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
        td.setImage(t.getImage());
        return td;
    }
}
