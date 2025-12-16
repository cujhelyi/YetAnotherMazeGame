package com.example.labyrinth.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:5173")
public class GameController {

    private List<String> players = new ArrayList<>();

    @GetMapping("/players")
    public List<String> getPlayers() {
        return players;
    }

    @PostMapping("/join")
    public String joinGame(@RequestParam String name) {
        players.add(name);
        return name + " joined the game!";
    }

    @DeleteMapping("/reset")
    public String resetGame() {
        players.clear();
        return "Game reset!";
    }
}
