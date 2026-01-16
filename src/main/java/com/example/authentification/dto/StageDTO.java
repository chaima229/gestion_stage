package com.example.authentification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageDTO {
    private Long id;
    private String sujet;
    private String description;
    private String entreprise;
    private String ville;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String etat;
    private Long etudiantId;
    private Long encadrantId;
    private Long filiereId;
    private String commentaire;
    
    // Informations enrichies de l'étudiant
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;
    
    // Informations enrichies de l'encadrant
    private String encadrantNom;
    private String encadrantPrenom;
    private String encadrantEmail;
    
    // Informations enrichies de la filière
    private String filiereNom;
    private String filiereCode;
    
    // Chemin du rapport
    private String rapportPath;
}

