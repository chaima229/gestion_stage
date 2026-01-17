package com.example.filiere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "filieres",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nom", "niveau"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;
    
    @Column(unique = true)
    private String code; // Code unique de la filière (ex: INFO, GC, etc.)

    @Column(nullable = false)
    private String niveau; // M1, M2

    @Column(columnDefinition = "TEXT")
    private String description;
}
