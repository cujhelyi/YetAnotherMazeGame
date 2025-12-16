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
        GameBoard gameBoard = boardService.createDefaultBoard();
        BoardDTO dto = toDTO(gameBoard);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(gameBoard.getId()).toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long id) {
        GameBoard gameBoard = boardService.loadBoard(id);
        if (gameBoard == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(gameBoard));
    }

    @PostMapping("/spare/rotate")
    public ResponseEntity<TileDTO> rotateSpare(@RequestParam(defaultValue = "1") int times,
                                               @RequestParam(defaultValue = "cw") String direction) {
        try {
            BoardTile tile;
            if ("ccw".equalsIgnoreCase(direction) || "counterclockwise".equalsIgnoreCase(direction)) {
                tile = boardService.rotateSpareCounterclockwise(times);
            } else {
                tile = boardService.rotateSpareClockwise(times);
            }
            return ResponseEntity.ok(toTileDTO(tile));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/shuffle")
    public ResponseEntity<BoardDTO> shuffleBoard(@PathVariable Long id, @RequestParam(required = false) Long seed) {
        try {
            GameBoard gameBoard = (seed == null) ? boardService.shuffleBoard(id) : boardService.shuffleBoard(id, seed);
            if (gameBoard == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(gameBoard));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/insert")
    public ResponseEntity<BoardDTO> insertSpare(@PathVariable Long id,
                                                @RequestParam(required = false) Integer row,
                                                @RequestParam(required = false) Integer col,
                                                @RequestParam String position) {
        try {
            GameBoard gameBoard = boardService.insertSpare(id, row, col, position);
            if (gameBoard == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(gameBoard));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{boardId}/players/{playerId}/move")
    public ResponseEntity<BoardDTO> movePlayer(@PathVariable Long boardId,
                                               @PathVariable Long playerId,
                                               @RequestParam int row,
                                               @RequestParam int col) {
        try {
            GameBoard gameBoard = boardService.movePlayer(boardId, playerId, row, col);
            if (gameBoard == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(gameBoard));
        } catch (IllegalArgumentException | IllegalStateException e) {
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
        List<com.example.labyrinth.dto.PlayerDTO> players = gb.getPlayersList().stream().map(this::toPlayerDTO).collect(Collectors.toList());
        dto.setPlayers(players);
        return dto;
    }

    private TileDTO toTileDTO(BoardTile tile) {
        TileDTO tileDTO = new TileDTO();
        tileDTO.setId(tile.getId());
        tileDTO.setRowIndex(tile.getRowIndex());
        tileDTO.setColIndex(tile.getColIndex());
        tileDTO.setExitNorth(tile.isExitNorth());
        tileDTO.setExitEast(tile.isExitEast());
        tileDTO.setExitSouth(tile.isExitSouth());
        tileDTO.setExitWest(tile.isExitWest());
        tileDTO.setTreasure(tile.getTreasure());
        tileDTO.setImage(tile.getImage());
        tileDTO.setRotation(tile.getRotation());
        return tileDTO;
    }

    private com.example.labyrinth.dto.PlayerDTO toPlayerDTO(com.example.labyrinth.entity.Player player) {
        com.example.labyrinth.dto.PlayerDTO playerDTO = new com.example.labyrinth.dto.PlayerDTO();
        playerDTO.setId(player.getId());
        playerDTO.setPlayerNumber(player.getPlayerNumber());
        playerDTO.setRowIndex(player.getRowIndex());
        playerDTO.setColIndex(player.getColIndex());
        return playerDTO;
    }
}
