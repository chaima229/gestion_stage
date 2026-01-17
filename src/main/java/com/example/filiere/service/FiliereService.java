package com.example.filiere.service;

import com.example.filiere.entity.Filiere;
import com.example.filiere.repository.FiliereRepository;
import com.example.authentification.dto.FiliereDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository;

    public FiliereService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    public FiliereDTO createFiliere(FiliereDTO dto) {
        Filiere filiere = Filiere.builder()
                .nom(dto.getNom())
                .niveau(dto.getNiveau())
                .description(dto.getDescription())
                .build();

        filiere = filiereRepository.save(filiere);
        return convertToDTO(filiere);
    }

    public FiliereDTO getFiliere(Long id) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filiere not found"));
        return convertToDTO(filiere);
    }

    public List<FiliereDTO> getAllFilieres() {
        return filiereRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FiliereDTO> getFilieresByNiveau(String niveau) {
        return filiereRepository.findByNiveau(niveau)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FiliereDTO updateFiliere(Long id, FiliereDTO dto) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filiere not found"));

        filiere.setNom(dto.getNom());
        filiere.setNiveau(dto.getNiveau());
        filiere.setDescription(dto.getDescription());

        filiere = filiereRepository.save(filiere);
        return convertToDTO(filiere);
    }

    public void deleteFiliere(Long id) {
        filiereRepository.deleteById(id);
    }

    public com.example.authentification.dto.ImportResultDTO importFilieres(List<FiliereDTO> filiereDTOs) {
        int success = 0;
        int errors = 0;

        System.out.println("=== DÉBUT IMPORT FILIÈRES ===");
        System.out.println("Nombre de filières à importer: " + filiereDTOs.size());

        for (FiliereDTO dto : filiereDTOs) {
            try {
                System.out.println("Traitement de: " + dto.getNom() + " - Niveau: " + dto.getNiveau());
                
                // Vérifier si une filière avec le même nom ET niveau existe déjà
                boolean exists = filiereRepository.findByNomAndNiveau(dto.getNom(), dto.getNiveau()).isPresent();
                
                if (!exists) {
                    Filiere filiere = Filiere.builder()
                            .nom(dto.getNom())
                            .niveau(dto.getNiveau())
                            .description(dto.getDescription())
                            .build();
                    filiereRepository.save(filiere);
                    success++;
                    System.out.println("✓ Filière ajoutée: " + dto.getNom());
                } else {
                    // Filière existe déjà, compter comme erreur
                    errors++;
                    System.out.println("✗ Filière existe déjà (ignorée): " + dto.getNom());
                }
            } catch (Exception e) {
                errors++;
                System.out.println("✗ Erreur lors de l'import de: " + dto.getNom() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("=== FIN IMPORT ===");
        System.out.println("Succès: " + success + " | Erreurs: " + errors);

        return com.example.authentification.dto.ImportResultDTO.builder()
                .success(success)
                .errors(errors)
                .build();
    }

    private FiliereDTO convertToDTO(Filiere filiere) {
        return FiliereDTO.builder()
                .id(filiere.getId())
                .nom(filiere.getNom())
                .niveau(filiere.getNiveau())
                .description(filiere.getDescription())
                .build();
    }
}

