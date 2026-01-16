package com.example.stage.service;

import com.example.stage.entity.StageEtat;
import com.example.stage.repository.StageRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    private final StageRepository stageRepository;

    public StatsService(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        long totalStages = stageRepository.count();
        summary.put("totalStages", totalStages);

        Map<String, Long> etatCounts = getCountByEtat();
        summary.put("etatCounts", etatCounts);

        Map<String, Long> filiereCounts = getCountByFiliere();
        summary.put("filiereCounts", filiereCounts);

        return summary;
    }

    public Map<String, Long> getCountByEtat() {
        Map<String, Long> counts = new HashMap<>();
        for (StageEtat etat : StageEtat.values()) {
            counts.put(etat.name(), stageRepository.countByEtat(etat));
        }
        return counts;
    }

    public Map<String, Long> getCountByFiliere() {
        Map<String, Long> counts = new HashMap<>();
        List<Object[]> results = stageRepository.countByFiliere();
        for (Object[] row : results) {
            Long filiereId = (Long) row[0];
            Long count = (Long) row[1];
            counts.put("FILIERE_" + filiereId, count);
        }
        return counts;
    }
}
