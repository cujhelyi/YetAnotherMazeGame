package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    private String riskRating; // e.g., "LOW", "MEDIUM", "HIGH"

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // getters and setters
}
