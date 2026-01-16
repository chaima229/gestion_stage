package com.example.authentification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private Long totalStudents;
    private Long totalStages;
    private Long totalTeachers;
    private Long totalFilieres;
    private Map<String, Long> stagesByState;
    private List<RecentStageDto> recentStages;
    private Map<String, Long> stagesByFiliere;
    private Map<String, Long> topEntreprises;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentStageDto {
        private Long id;
        private String title;
        private String company;
        private String status;
        private String student;
    }
}
