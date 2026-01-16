package com.example.authentification.controller;

import com.example.authentification.entity.ImportLog;
import com.example.authentification.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur pour gérer les imports de fichiers Excel
 * Accessible uniquement aux SOUS_ADMIN
 */
@RestController
@RequestMapping("/api/imports")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private final ImportService importService;

    /**
     * Importer un fichier Excel contenant les comptes d'étudiants ou d'encadrants
     *
     * @param file Fichier Excel à importer
     * @param fileType ETUDIANT ou ENSEIGNANT
     * @return ImportLog avec le résultat de l'import
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<ImportLog> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Upload de fichier Excel: {} pour type: {}", file.getOriginalFilename(), fileType);

        // Valider le type de fichier
        if (!fileType.equals("ETUDIANT") && !fileType.equals("ENSEIGNANT")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ImportLog importLog = importService.importExcelFile(file, fileType, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(importLog);
        } catch (Exception e) {
            log.error("Erreur lors de l'upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer l'historique des imports d'un SOUS_ADMIN
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<ImportLog>> getImportHistory(
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Récupération de l'historique des imports pour l'utilisateur: {}", userId);

        List<ImportLog> imports = importService.getImportHistory(userId);
        return ResponseEntity.ok(imports);
    }

    /**
     * Récupérer les détails d'un import
     */
    @GetMapping("/{importId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<ImportLog> getImportDetails(
            @PathVariable Long importId) {

        log.info("Récupération des détails de l'import: {}", importId);

        try {
            ImportLog importLog = importService.getImportDetails(importId);
            return ResponseEntity.ok(importLog);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'import: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

