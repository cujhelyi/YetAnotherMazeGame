package com.example.labyrinth.repository;

import com.example.labyrinth.entity.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameBoardRepository extends JpaRepository<GameBoard, Long> {
}
