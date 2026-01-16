package com.example.stage.service;

import com.example.stage.entity.Stage;
import com.example.stage.entity.StageEtat;
import com.example.stage.repository.StageRepository;
import com.example.authentification.dto.StageDTO;
import com.example.authentification.entity.User;
import com.example.authentification.repository.UserRepository;
import com.example.filiere.entity.Filiere;
import com.example.filiere.repository.FiliereRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StageService {

    private final StageRepository stageRepository;
    private final UserRepository userRepository;
    private final FiliereRepository filiereRepository;

    public StageService(StageRepository stageRepository, 
                        UserRepository userRepository,
                        FiliereRepository filiereRepository) {
        this.stageRepository = stageRepository;
        this.userRepository = userRepository;
        this.filiereRepository = filiereRepository;
    }

    public StageDTO createStage(StageDTO dto, Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Utiliser l'ID de l'utilisateur connecté comme etudiantId
        Long etudiantId = dto.getEtudiantId() != null ? dto.getEtudiantId() : currentUser.getId();
        
        // Utiliser la filière de l'utilisateur si non fournie
        Long filiereId = dto.getFiliereId() != null ? dto.getFiliereId() : currentUser.getFiliereId();
        
        // Validation : vérifier que la filière est bien définie
        if (filiereId == null) {
            throw new RuntimeException("L'étudiant n'est associé à aucune filière. Veuillez contacter l'administrateur.");
        }
        
        Stage stage = Stage.builder()
                .sujet(dto.getSujet())
                .description(dto.getDescription())
                .entreprise(dto.getEntreprise())
                .ville(dto.getVille())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .etudiantId(etudiantId)
                .filiereId(filiereId)
                .etat(StageEtat.BROUILLON)
                .build();

        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }

    public StageDTO getStage(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));
        return convertToDTO(stage);
    }

    public List<StageDTO> getAllStages() {
        return stageRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StageDTO> getStagesByEtudiant(Long etudiantId) {
        return stageRepository.findByEtudiantId(etudiantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StageDTO> getStagesByEncadrant(Long encadrantId) {
        return stageRepository.findByEncadrantId(encadrantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StageDTO> getStagesByFiliere(Long filiereId) {
        return stageRepository.findByFiliereId(filiereId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StageDTO> getStagesByEtat(String etat) {
        return stageRepository.findByEtat(StageEtat.valueOf(etat))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StageDTO> getStagesByEntreprise(String entreprise) {
        return stageRepository.findByEntreprise(entreprise)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les stages en attente de validation pour un encadrant (par filière)
     */
    public List<StageDTO> getStagesEnAttenteByFiliere(Long filiereId) {
        return stageRepository.findByFiliereIdAndEtat(filiereId, StageEtat.EN_ATTENTE_VALIDATION)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer tous les stages en attente de validation
     */
    public List<StageDTO> getAllStagesEnAttente() {
        return stageRepository.findByEtat(StageEtat.EN_ATTENTE_VALIDATION)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StageDTO updateStage(Long id, StageDTO dto) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.BROUILLON && stage.getEtat() != StageEtat.REFUSE) {
            throw new RuntimeException("Cannot update stage in state: " + stage.getEtat());
        }

        stage.setSujet(dto.getSujet());
        stage.setDescription(dto.getDescription());
        stage.setEntreprise(dto.getEntreprise());
        stage.setVille(dto.getVille());
        stage.setDateDebut(dto.getDateDebut());
        stage.setDateFin(dto.getDateFin());

        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }

    public StageDTO submitForValidation(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.BROUILLON && stage.getEtat() != StageEtat.REFUSE) {
            throw new RuntimeException("Only BROUILLON or REFUSE stages can be submitted");
        }

        stage.setEtat(StageEtat.EN_ATTENTE_VALIDATION);
        stage.setCommentaire(null); // Effacer l'ancien commentaire de refus
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }

    public StageDTO validateStage(Long id, Long encadrantId) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.EN_ATTENTE_VALIDATION) {
            throw new RuntimeException("Only EN_ATTENTE_VALIDATION stages can be validated");
        }

        stage.setEtat(StageEtat.VALIDE);
        stage.setEncadrantId(encadrantId);
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }

    public StageDTO refuseStage(Long id, String commentaire) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.EN_ATTENTE_VALIDATION) {
            throw new RuntimeException("Only EN_ATTENTE_VALIDATION stages can be refused");
        }

        stage.setEtat(StageEtat.REFUSE);
        stage.setCommentaire(commentaire);
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }
    
    /**
     * Démarrer un stage (passer de VALIDE à EN_COURS)
     */
    public StageDTO startStage(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.VALIDE) {
            throw new RuntimeException("Only VALIDE stages can be started");
        }

        stage.setEtat(StageEtat.EN_COURS);
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }
    
    /**
     * Terminer un stage (passer de EN_COURS à TERMINE)
     */
    public StageDTO completeStage(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.EN_COURS) {
            throw new RuntimeException("Only EN_COURS stages can be completed");
        }

        stage.setEtat(StageEtat.TERMINE);
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }
    
    /**
     * Marquer un stage comme soutenu
     */
    public StageDTO markAsSoutenu(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getEtat() != StageEtat.TERMINE) {
            throw new RuntimeException("Only TERMINE stages can be marked as soutenu");
        }

        stage.setEtat(StageEtat.SOUTENU);
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }
    
    /**
     * Ajouter/Mettre à jour le chemin du rapport
     */
    public StageDTO updateRapport(Long id, String rapportPath) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        stage.setRapportPath(rapportPath);
        stage = stageRepository.save(stage);
        return convertToDTO(stage);
    }

    public void deleteStage(Long id) {
        stageRepository.deleteById(id);
    }

    /**
     * Recherche combinée avec plusieurs critères
     */
    public List<StageDTO> searchStages(Long filiereId, String etat, String entreprise, Integer annee) {
        List<Stage> stages = stageRepository.findAll();
        
        return stages.stream()
                .filter(stage -> filiereId == null || stage.getFiliereId().equals(filiereId))
                .filter(stage -> etat == null || etat.isEmpty() || stage.getEtat().name().equals(etat))
                .filter(stage -> entreprise == null || entreprise.isEmpty() 
                        || stage.getEntreprise().toLowerCase().contains(entreprise.toLowerCase()))
                .filter(stage -> annee == null || (stage.getDateDebut() != null 
                        && stage.getDateDebut().getYear() == annee))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private StageDTO convertToDTO(Stage stage) {
        StageDTO.StageDTOBuilder builder = StageDTO.builder()
                .id(stage.getId())
                .sujet(stage.getSujet())
                .description(stage.getDescription())
                .entreprise(stage.getEntreprise())
                .ville(stage.getVille())
                .dateDebut(stage.getDateDebut())
                .dateFin(stage.getDateFin())
                .etat(stage.getEtat().name())
                .etudiantId(stage.getEtudiantId())
                .encadrantId(stage.getEncadrantId())
                .filiereId(stage.getFiliereId())
                .commentaire(stage.getCommentaire())
                .rapportPath(stage.getRapportPath());
        
        // Enrichir avec les informations de l'étudiant
        if (stage.getEtudiantId() != null) {
            Optional<User> etudiant = userRepository.findById(stage.getEtudiantId());
            etudiant.ifPresent(user -> {
                builder.etudiantNom(user.getNom());
                builder.etudiantPrenom(user.getPrenom());
                builder.etudiantEmail(user.getEmail());
            });
        }
        
        // Enrichir avec les informations de l'encadrant
        if (stage.getEncadrantId() != null) {
            Optional<User> encadrant = userRepository.findById(stage.getEncadrantId());
            encadrant.ifPresent(user -> {
                builder.encadrantNom(user.getNom());
                builder.encadrantPrenom(user.getPrenom());
                builder.encadrantEmail(user.getEmail());
            });
        }
        
        // Enrichir avec les informations de la filière
        if (stage.getFiliereId() != null) {
            Optional<Filiere> filiere = filiereRepository.findById(stage.getFiliereId());
            filiere.ifPresent(f -> {
                builder.filiereNom(f.getNom());
                // Utiliser code s'il existe, sinon niveau
                builder.filiereCode(f.getCode() != null ? f.getCode() : f.getNiveau());
            });
        }
        
        return builder.build();
    }
}
