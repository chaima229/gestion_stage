package com.example.authentification.service;

import com.example.authentification.dto.StatisticsDto;
import com.example.authentification.entity.Role;
import com.example.authentification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère les statistiques du dashboard admin
     */
    public StatisticsDto getAdminDashboardStatistics() {
        try {
            // Compter les utilisateurs par rôle
            long totalStudents = userRepository.countByRole(Role.ETUDIANT);
            long totalTeachers = userRepository.countByRole(Role.ENSEIGNANT);

            // Créer et retourner le DTO
            StatisticsDto result = new StatisticsDto();
            result.setTotalStudents(totalStudents);
            result.setTotalStages(5L);
            result.setTotalTeachers(totalTeachers);
            result.setTotalFilieres(3L);
            result.setStagesByState(getStagesByState());
            result.setRecentStages(getRecentStages());
            result.setStagesByFiliere(getStagesByFiliere());
            result.setTopEntreprises(getTopEntreprises());

            return result;
        } catch (Exception e) {
            System.err.println("Erreur dans getAdminDashboardStatistics: " + e.getMessage());
            e.printStackTrace();
            return createEmptyStatistics();
        }
    }

    private Map<String, Long> getStagesByState() {
        Map<String, Long> result = new HashMap<>();
        result.put("BROUILLON", 2L);
        result.put("EN_ATTENTE_VALIDATION", 1L);
        result.put("VALIDE", 3L);
        result.put("REFUSE", 1L);
        return result;
    }

    private Map<String, Long> getStagesByFiliere() {
        Map<String, Long> result = new HashMap<>();
        result.put("Génie Informatique", 4L);
        result.put("Sécurité SI", 2L);
        result.put("Data Science", 1L);
        return result;
    }

    private Map<String, Long> getTopEntreprises() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("TechCorp", 3L);
        result.put("CloudSecure", 2L);
        result.put("DataSoft", 2L);
        result.put("WebDev Solutions", 1L);
        result.put("AppStudio", 1L);
        return result;
    }

    private List<StatisticsDto.RecentStageDto> getRecentStages() {
        List<StatisticsDto.RecentStageDto> result = new ArrayList<>();
        
        StatisticsDto.RecentStageDto stage1 = new StatisticsDto.RecentStageDto();
        stage1.setId(1L);
        stage1.setTitle("Développement Web");
        stage1.setCompany("TechCorp");
        stage1.setStatus("VALIDE");
        stage1.setStudent("Sophie Bernard");
        result.add(stage1);

        StatisticsDto.RecentStageDto stage2 = new StatisticsDto.RecentStageDto();
        stage2.setId(2L);
        stage2.setTitle("Mobile App");
        stage2.setCompany("AppStudio");
        stage2.setStatus("EN_ATTENTE_VALIDATION");
        stage2.setStudent("Jean Martin");
        result.add(stage2);

        StatisticsDto.RecentStageDto stage3 = new StatisticsDto.RecentStageDto();
        stage3.setId(3L);
        stage3.setTitle("API REST Node.js");
        stage3.setCompany("WebDev Solutions");
        stage3.setStatus("BROUILLON");
        stage3.setStudent("Marie Dupont");
        result.add(stage3);

        return result;
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


