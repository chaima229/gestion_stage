package com.example.authentification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité pour enregistrer les imports Excel de comptes
 * Utilisée par SOUS_ADMIN pour tracer les uploads et créations de comptes
 */
@Entity
@Table(name = "import_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType; // ETUDIANT, ENSEIGNANT

    @Column(nullable = false)
    private Integer totalRows;

    @Column(nullable = false)
    private Integer successCount;

    @Column(nullable = false)
    private Integer failureCount;

    @Column(columnDefinition = "LONGTEXT")
    private String errorDetails;

    @Column(nullable = false)
    private Long uploadedByUserId; // ID du SOUS_ADMIN qui a fait l'upload

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status; // PENDING, SUCCESS, PARTIAL_SUCCESS, FAILED

    /**
     * Statuts possibles pour un import
     */
    public enum ImportStatus {
        PENDING,            // En cours de traitement
        SUCCESS,            // Tous les comptes créés avec succès
        PARTIAL_SUCCESS,    // Certains comptes créés, d'autres échoués
        FAILED              // Tous les comptes ont échoué
    }
}

