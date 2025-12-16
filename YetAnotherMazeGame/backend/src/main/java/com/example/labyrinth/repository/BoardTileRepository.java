package com.example.labyrinth.repository;

import com.example.labyrinth.entity.BoardTile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTileRepository extends JpaRepository<BoardTile, Long> {
}
