package com.example.labyrinth.repository;

import com.example.labyrinth.entity.BoardTile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardTileRepository extends JpaRepository<BoardTile, Long> {
	Optional<BoardTile> findFirstByRowIndexAndColIndex(int rowIndex, int colIndex);
}
