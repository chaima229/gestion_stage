package com.example.stage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sujet;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String entreprise;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StageEtat etat = StageEtat.BROUILLON;

    @Column(nullable = false)
    private Long etudiantId;

    @Column(name = "encadrant_id")
    private Long encadrantId;

    @Column(nullable = false)
    private Long filiereId;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "rapport_path")
    private String rapportPath;
}
