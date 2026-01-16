package com.example.authentification.service;

import com.example.authentification.entity.ImportLog;
import com.example.authentification.entity.User;
import com.example.authentification.entity.Role;
import com.example.authentification.repository.ImportLogRepository;
import com.example.authentification.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service pour gérer les imports de fichiers Excel
 * Utilisé par les SOUS_ADMIN pour importer des comptes d'étudiants et d'encadrants
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    private final ImportLogRepository importLogRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Importer un fichier Excel contenant les comptes d'étudiants ou d'encadrants
     *
     * @param file Fichier Excel à importer
     * @param fileType ETUDIANT ou ENSEIGNANT
     * @param subAdminId ID du SOUS_ADMIN qui fait l'import
     * @return ImportLog avec le résultat de l'import
     */
    public ImportLog importExcelFile(MultipartFile file, String fileType, Long subAdminId) {
        log.info("Démarrage de l'import: {} par utilisateur {}", fileType, subAdminId);

        // Créer un log d'import
        ImportLog importLog = ImportLog.builder()
                .fileName(file.getOriginalFilename())
                .fileType(fileType)
                .uploadedByUserId(subAdminId)
                .uploadedAt(LocalDateTime.now())
                .filePath("uploads/imports/" + UUID.randomUUID() + "/" + file.getOriginalFilename())
                .status(ImportLog.ImportStatus.PENDING)
                .successCount(0)
                .failureCount(0)
                .build();

        try {
            // Valider le fichier
            validateFile(file);

            // Parser le fichier Excel
            List<ImportRecord> records = parseExcelFile(file);
            importLog.setTotalRows(records.size());

            // Traiter les enregistrements
            processRecords(records, fileType, importLog);

            // Déterminer le statut
            if (importLog.getFailureCount() == 0) {
                importLog.setStatus(ImportLog.ImportStatus.SUCCESS);
                log.info("Import réussi: {} enregistrements créés", importLog.getSuccessCount());
            } else if (importLog.getSuccessCount() > 0) {
                importLog.setStatus(ImportLog.ImportStatus.PARTIAL_SUCCESS);
                log.warn("Import partiellement réussi: {} succès, {} erreurs",
                    importLog.getSuccessCount(), importLog.getFailureCount());
            } else {
                importLog.setStatus(ImportLog.ImportStatus.FAILED);
                log.error("Import échoué: {} erreurs", importLog.getFailureCount());
            }

        } catch (Exception e) {
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorDetails(e.getMessage());
            log.error("Erreur lors de l'import: {}", e.getMessage(), e);
        }

        // Sauvegarder le log
        return importLogRepository.save(importLog);
    }

    /**
     * Valider le fichier uploadé
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("Le fichier doit être un fichier Excel (.xlsx ou .xls)");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB max
            throw new IllegalArgumentException("Le fichier ne doit pas dépasser 10MB");
        }
    }

    /**
     * Parser le fichier Excel et extraire les enregistrements
     *
     * Format attendu:
     * - Colonne A: Email
     * - Colonne B: Nom
     * - Colonne C: Prénom
     * - Colonne D: Filière (optionnel pour ENSEIGNANT)
     * - Colonne E: Niveau M1/M2 (optionnel)
     */
    private List<ImportRecord> parseExcelFile(MultipartFile file) throws IOException {
        List<ImportRecord> records = new ArrayList<>();

        // TODO: Implémenter le parsing Excel avec Apache POI
        // Pour maintenant, retourner une liste vide
        // Le parsing sera implémenté avec: org.apache.poi:poi-ooxml

        log.debug("Parsing du fichier Excel: {}", file.getOriginalFilename());
        return records;
    }

    /**
     * Traiter les enregistrements et créer les comptes
     */
    private void processRecords(List<ImportRecord> records, String fileType, ImportLog importLog) {
        StringBuilder errorDetails = new StringBuilder();

        for (ImportRecord record : records) {
            try {
                // Vérifier si l'email existe déjà
                if (userRepository.existsByEmail(record.getEmail())) {
                    importLog.setFailureCount(importLog.getFailureCount() + 1);
                    errorDetails.append("Email déjà existant: ").append(record.getEmail()).append("\n");
                    continue;
                }

                // Créer le nouvel utilisateur
                User user = User.builder()
                        .email(record.getEmail())
                        .nom(record.getNom())
                        .prenom(record.getPrenom())
                        .role(fileType.equals("ETUDIANT") ? Role.ETUDIANT : Role.ENSEIGNANT)
                        .filiereId(record.getFiliereId())
                        .yearLevel(record.getYearLevel())
                        .password("") // À définir par l'utilisateur au premier login
                        .build();

                // Générer un mot de passe temporaire (optionnel)
                String tempPassword = generateTemporaryPassword();
                // user.setPassword(tempPassword); // À implémenter avec BCrypt

                userRepository.save(user);
                importLog.setSuccessCount(importLog.getSuccessCount() + 1);

                log.debug("Compte créé: {} ({})", record.getEmail(), record.getNom());

            } catch (Exception e) {
                importLog.setFailureCount(importLog.getFailureCount() + 1);
                errorDetails.append("Erreur pour ").append(record.getEmail()).append(": ")
                        .append(e.getMessage()).append("\n");
                log.error("Erreur lors de la création du compte pour {}: {}",
                    record.getEmail(), e.getMessage());
            }
        }

        if (errorDetails.length() > 0) {
            importLog.setErrorDetails(errorDetails.toString());
        }
    }

    /**
     * Générer un mot de passe temporaire
     */
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 12);
    }

    /**
     * Récupérer l'historique des imports d'un SOUS_ADMIN
     */
    public List<ImportLog> getImportHistory(Long subAdminId) {
        return importLogRepository.findByUploadedByUserId(subAdminId);
    }

    /**
     * Récupérer les détails d'un import
     */
    public ImportLog getImportDetails(Long importId) {
        return importLogRepository.findById(importId)
                .orElseThrow(() -> new RuntimeException("Import not found"));
    }

    /**
     * Classe interne pour représenter un enregistrement du fichier Excel
     */
    public static class ImportRecord {
        private String email;
        private String nom;
        private String prenom;
        private Long filiereId;
        private String yearLevel;

        // Getters et setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }

        public Long getFiliereId() { return filiereId; }
        public void setFiliereId(Long filiereId) { this.filiereId = filiereId; }

        public String getYearLevel() { return yearLevel; }
        public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
    }
}

