package com.example.authentification.service;

import com.example.authentification.dto.StatisticsDto;
import com.example.authentification.entity.Role;
import com.example.authentification.repository.UserRepository;
import com.example.stage.repository.StageRepository;
import com.example.filiere.repository.FiliereRepository;
import com.example.stage.entity.Stage;
import com.example.stage.entity.StageEtat;
import com.example.filiere.entity.Filiere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    /**
     * Récupère les statistiques du dashboard admin
     */
    public StatisticsDto getAdminDashboardStatistics() {
        try {
            long totalStudents = userRepository.countByRole(Role.ETUDIANT);
            long totalTeachers = userRepository.countByRole(Role.ENSEIGNANT);
            long totalStages = stageRepository.count();
            long totalFilieres = filiereRepository.count();

            // Répartition par état
            Map<String, Long> stagesByState = new LinkedHashMap<>();
            for (StageEtat etat : StageEtat.values()) {
                stagesByState.put(etat.name(), stageRepository.countByEtat(etat));
            }

            // Répartition par filière
            Map<String, Long> stagesByFiliere = new LinkedHashMap<>();
            filiereRepository.findAll().forEach(f -> {
                long count = stageRepository.findByFiliereId(f.getId()).size();
                stagesByFiliere.put(f.getNom(), count);
            });

            // Top entreprises
            Map<String, Long> topEntreprises = new LinkedHashMap<>();
            stageRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(Stage::getEntreprise, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> topEntreprises.put(e.getKey(), e.getValue()));

            // Stages récents (les 3 derniers)
            List<Stage> recentStages = stageRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
            List<StatisticsDto.RecentStageDto> recentStageDtos = recentStages.stream().limit(3).map(s -> {
                StatisticsDto.RecentStageDto dto = new StatisticsDto.RecentStageDto();
                dto.setId(s.getId());
                dto.setTitle(s.getSujet());
                dto.setCompany(s.getEntreprise());
                dto.setStatus(s.getEtat().name());
                dto.setStudent(""); // À compléter si tu as la jointure avec l'étudiant
                return dto;
            }).toList();

            StatisticsDto result = new StatisticsDto();
            result.setTotalStudents(totalStudents);
            result.setTotalStages(totalStages);
            result.setTotalTeachers(totalTeachers);
            result.setTotalFilieres(totalFilieres);
            result.setStagesByState(stagesByState);
            result.setRecentStages(recentStageDtos);
            result.setStagesByFiliere(stagesByFiliere);
            result.setTopEntreprises(topEntreprises);

            return result;
        } catch (Exception e) {
            System.err.println("Erreur dans getAdminDashboardStatistics: " + e.getMessage());
            e.printStackTrace();
            return createEmptyStatistics();
        }
    }

    private StatisticsDto createEmptyStatistics() {
        StatisticsDto empty = new StatisticsDto();
        empty.setTotalStudents(0L);
        empty.setTotalStages(0L);
        empty.setTotalTeachers(0L);
        empty.setTotalFilieres(0L);
        empty.setStagesByState(new HashMap<>());
        empty.setRecentStages(new ArrayList<>());
        empty.setStagesByFiliere(new HashMap<>());
        empty.setTopEntreprises(new HashMap<>());
        return empty;
    }
}


