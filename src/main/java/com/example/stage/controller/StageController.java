package com.example.stage.controller;

import com.example.stage.service.StageService;
import com.example.authentification.dto.StageDTO;
import com.example.authentification.entity.User;
import com.example.authentification.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stages")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class StageController {

    private final StageService stageService;
    private final UserRepository userRepository;

    public StageController(StageService stageService, UserRepository userRepository) {
        this.stageService = stageService;
        this.userRepository = userRepository;
    }

    // ==================== ENDPOINTS ÉTUDIANTS ====================
    
    /**
     * Récupérer mes stages (étudiant connecté) - DOIT ÊTRE AVANT /{id}
     */
    @GetMapping("/my-stages")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public ResponseEntity<List<StageDTO>> getMyStages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return ResponseEntity.ok(stageService.getStagesByEtudiant(user.getId()));
    }
    
    /**
     * Récupérer les stages à valider (enseignant connecté) - DOIT ÊTRE AVANT /{id}
     */
    @GetMapping("/to-validate")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesToValidate() {
        // Retourne tous les stages en attente de validation
        return ResponseEntity.ok(stageService.getAllStagesEnAttente());
    }
    
    /**
     * Créer un nouveau stage (étudiant uniquement)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public ResponseEntity<StageDTO> createStage(@Valid @RequestBody StageDTO dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stageService.createStage(dto, authentication));
    }
    
    /**
     * Soumettre un stage pour validation (étudiant)
     */
    @PutMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public ResponseEntity<StageDTO> submitForValidation(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.submitForValidation(id));
    }
    
    /**
     * Récupérer les stages d'un étudiant
     */
    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(stageService.getStagesByEtudiant(etudiantId));
    }
    
    /**
     * Récupérer mes stages (endpoint pour l'étudiant connecté) avec ID explicite
     */
    @GetMapping("/mes-stages/{etudiantId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public ResponseEntity<List<StageDTO>> getMesStages(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(stageService.getStagesByEtudiant(etudiantId));
    }

    // ==================== ENDPOINTS ENCADRANTS (ENSEIGNANTS) ====================
    
    /**
     * Récupérer les stages assignés à un encadrant
     */
    @GetMapping("/encadrant/{encadrantId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesByEncadrant(@PathVariable Long encadrantId) {
        return ResponseEntity.ok(stageService.getStagesByEncadrant(encadrantId));
    }
    
    /**
     * Récupérer les stages en attente de validation pour une filière
     */
    @GetMapping("/en-attente/filiere/{filiereId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesEnAttenteByFiliere(@PathVariable Long filiereId) {
        return ResponseEntity.ok(stageService.getStagesEnAttenteByFiliere(filiereId));
    }
    
    /**
     * Récupérer tous les stages en attente de validation
     */
    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getAllStagesEnAttente() {
        return ResponseEntity.ok(stageService.getAllStagesEnAttente());
    }

    /**
     * Valider un stage (encadrant)
     */
    @PutMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<StageDTO> validateStage(@PathVariable Long id, @RequestParam Long encadrantId) {
        return ResponseEntity.ok(stageService.validateStage(id, encadrantId));
    }

    /**
     * Refuser un stage (encadrant)
     */
    @PutMapping("/{id}/refuse")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<StageDTO> refuseStage(@PathVariable Long id, @RequestParam String commentaire) {
        return ResponseEntity.ok(stageService.refuseStage(id, commentaire));
    }
    
    /**
     * Démarrer un stage (passer à EN_COURS)
     */
    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<StageDTO> startStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.startStage(id));
    }
    
    /**
     * Marquer un stage comme terminé
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<StageDTO> completeStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.completeStage(id));
    }
    
    /**
     * Marquer un stage comme soutenu
     */
    @PutMapping("/{id}/soutenu")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<StageDTO> markAsSoutenu(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.markAsSoutenu(id));
    }

    // ==================== ENDPOINTS ADMIN ====================
    
    /**
     * Récupérer tous les stages (admin)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOUS_ADMIN', 'ENSEIGNANT')")
    public ResponseEntity<List<StageDTO>> getAllStages() {
        return ResponseEntity.ok(stageService.getAllStages());
    }

    /**
     * Récupérer un stage par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<StageDTO> getStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.getStage(id));
    }

    /**
     * Récupérer les stages par filière
     */
    @GetMapping("/filiere/{filiereId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesByFiliere(@PathVariable Long filiereId) {
        return ResponseEntity.ok(stageService.getStagesByFiliere(filiereId));
    }

    /**
     * Recherche par état
     */
    @GetMapping("/search/etat")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesByEtat(@RequestParam String etat) {
        return ResponseEntity.ok(stageService.getStagesByEtat(etat));
    }

    /**
     * Recherche par entreprise
     */
    @GetMapping("/search/entreprise")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> getStagesByEntreprise(@RequestParam String entreprise) {
        return ResponseEntity.ok(stageService.getStagesByEntreprise(entreprise));
    }

    /**
     * Recherche combinée avec plusieurs critères
     * Exemple: /api/stages/search?filiere=1&etat=VALIDE&entreprise=tech&annee=2026
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<List<StageDTO>> searchStages(
            @RequestParam(required = false) Long filiere,
            @RequestParam(required = false) String etat,
            @RequestParam(required = false) String entreprise,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(stageService.searchStages(filiere, etat, entreprise, annee));
    }

    /**
     * Mettre à jour un stage (étudiant ou admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public ResponseEntity<StageDTO> updateStage(@PathVariable Long id, @Valid @RequestBody StageDTO dto) {
        return ResponseEntity.ok(stageService.updateStage(id, dto));
    }
    
    /**
     * Mettre à jour le chemin du rapport
     */
    @PutMapping("/{id}/rapport")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<StageDTO> updateRapport(@PathVariable Long id, @RequestParam String rapportPath) {
        return ResponseEntity.ok(stageService.updateRapport(id, rapportPath));
    }

    /**
     * Upload du rapport de stage (fichier PDF)
     */
    @PostMapping("/{id}/rapport/upload")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadRapport(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // Validation du fichier
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Le fichier est vide"));
            }

            // Vérifier l'extension PDF
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Seuls les fichiers PDF sont acceptés"));
            }

            // Créer le dossier uploads s'il n'existe pas
            String uploadDir = "uploads/rapports";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "rapport_" + id + "_" + UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(newFilename);

            // Sauvegarder le fichier
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Mettre à jour le stage avec le chemin du rapport
            String rapportPath = uploadDir + "/" + newFilename;
            stageService.updateRapport(id, rapportPath);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Rapport uploadé avec succès");
            response.put("filename", newFilename);
            response.put("path", rapportPath);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'upload du fichier: " + e.getMessage()));
        }
    }

    /**
     * Télécharger le rapport de stage
     */
    @GetMapping("/{id}/rapport/download")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMIN', 'SOUS_ADMIN')")
    public ResponseEntity<Resource> downloadRapport(@PathVariable Long id) {
        try {
            // Récupérer le stage pour obtenir le chemin du rapport
            StageDTO stage = stageService.getStage(id);
            
            if (stage.getRapportPath() == null || stage.getRapportPath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Construire le chemin absolu du fichier
            // Le chemin dans la DB est relatif (uploads/rapports/...)
            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            Path filePath = uploadDir.getParent().resolve(stage.getRapportPath()).normalize();
            
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("Fichier introuvable ou illisible: " + filePath.toString());
                return ResponseEntity.notFound().build();
            }

            // Déterminer le nom du fichier pour le téléchargement
            String filename = filePath.getFileName().toString();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprimer un stage (admin uniquement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStage(@PathVariable Long id) {
        stageService.deleteStage(id);
        return ResponseEntity.noContent().build();
    }
}
