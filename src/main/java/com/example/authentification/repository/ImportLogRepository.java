package com.example.authentification.repository;

import com.example.authentification.entity.ImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImportLogRepository extends JpaRepository<ImportLog, Long> {

    /**
     * Trouver tous les imports d'un utilisateur SOUS_ADMIN
     */
    List<ImportLog> findByUploadedByUserId(Long userId);

    /**
     * Trouver les imports par type (ETUDIANT, ENSEIGNANT)
     */
    List<ImportLog> findByFileType(String fileType);

    /**
     * Trouver les imports par statut
     */
    List<ImportLog> findByStatus(ImportLog.ImportStatus status);

    /**
     * Trouver les imports d'un SOUS_ADMIN dans une plage de dates
     */
    List<ImportLog> findByUploadedByUserIdAndUploadedAtBetween(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Compter les imports réussis pour un SOUS_ADMIN
     */
    long countByUploadedByUserIdAndStatus(Long userId, ImportLog.ImportStatus status);
}

