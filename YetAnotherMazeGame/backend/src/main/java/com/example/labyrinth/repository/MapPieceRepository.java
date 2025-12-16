package com.example.labyrinth.repository;

import com.example.labyrinth.entity.MapPiece;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapPieceRepository extends JpaRepository<MapPiece, Integer> {
}
