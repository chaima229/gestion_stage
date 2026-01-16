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

    private FiliereDTO convertToDTO(Filiere filiere) {
        return FiliereDTO.builder()
                .id(filiere.getId())
                .nom(filiere.getNom())
                .niveau(filiere.getNiveau())
                .description(filiere.getDescription())
                .build();
    }
}

