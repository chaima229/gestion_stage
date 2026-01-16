package com.example.authentification.controller;

import com.example.authentification.dto.StatisticsDto;
import com.example.authentification.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Récupère les statistiques du dashboard admin
     */
    @GetMapping("/admin/dashboard")
    public ResponseEntity<?> getAdminDashboardStatistics() {
        try {
            System.out.println("=== StatisticsController.getAdminDashboardStatistics() appelé ===");
            StatisticsDto statistics = statisticsService.getAdminDashboardStatistics();
            System.out.println("Statistiques récupérées avec succès: " + statistics);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("ERREUR dans getAdminDashboardStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Récupère les statistiques du dashboard étudiant
     */
    @GetMapping("/student/dashboard")
    public ResponseEntity<?> getStudentDashboardStatistics(Authentication authentication) {
        try {
            System.out.println("=== StatisticsController.getStudentDashboardStatistics() appelé ===");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStages", 2);
            
            // stagesByState - format attendu par le frontend
            List<Map<String, Object>> stagesByState = new ArrayList<>();
            
            Map<String, Object> brouillon = new HashMap<>();
            brouillon.put("state", "BROUILLON");
            brouillon.put("count", 1);
            brouillon.put("percentage", 50.0);
            stagesByState.add(brouillon);
            
            Map<String, Object> enAttente = new HashMap<>();
            enAttente.put("state", "EN_ATTENTE_VALIDATION");
            enAttente.put("count", 1);
            enAttente.put("percentage", 50.0);
            stagesByState.add(enAttente);
            
            stats.put("stagesByState", stagesByState);
            stats.put("lastStageUpdate", new Date());
            stats.put("nextDeadline", null);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("ERREUR dans getStudentDashboardStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Récupère les statistiques du dashboard enseignant
     */
    @GetMapping("/teacher/dashboard")
    public ResponseEntity<?> getTeacherDashboardStatistics(Authentication authentication) {
        try {
            System.out.println("=== StatisticsController.getTeacherDashboardStatistics() appelé ===");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStagesToValidate", 3);
            stats.put("stagesValidated", 5);
            stats.put("stagesRefused", 1);
            stats.put("totalStudents", 15);
            
            // stagesByState
            List<Map<String, Object>> stagesByState = new ArrayList<>();
            
            Map<String, Object> enAttente = new HashMap<>();
            enAttente.put("state", "EN_ATTENTE_VALIDATION");
            enAttente.put("count", 3);
            enAttente.put("percentage", 33.3);
            stagesByState.add(enAttente);
            
            Map<String, Object> valide = new HashMap<>();
            valide.put("state", "VALIDE");
            valide.put("count", 5);
            valide.put("percentage", 55.6);
            stagesByState.add(valide);
            
            Map<String, Object> refuse = new HashMap<>();
            refuse.put("state", "REFUSE");
            refuse.put("count", 1);
            refuse.put("percentage", 11.1);
            stagesByState.add(refuse);
            
            stats.put("stagesByState", stagesByState);
            
            // stagesByFiliere
            List<Map<String, Object>> stagesByFiliere = new ArrayList<>();
            
            Map<String, Object> filiere1 = new HashMap<>();
            filiere1.put("filiereId", 1);
            filiere1.put("filiereName", "Génie Informatique");
            filiere1.put("count", 5);
            filiere1.put("percentage", 55.6);
            stagesByFiliere.add(filiere1);
            
            Map<String, Object> filiere2 = new HashMap<>();
            filiere2.put("filiereId", 2);
            filiere2.put("filiereName", "Sécurité SI");
            filiere2.put("count", 4);
            filiere2.put("percentage", 44.4);
            stagesByFiliere.add(filiere2);
            
            stats.put("stagesByFiliere", stagesByFiliere);
            stats.put("generatedAt", new Date());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("ERREUR dans getTeacherDashboardStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Récupère les statistiques de la plateforme
     */
    @GetMapping("/platform")
    public ResponseEntity<?> getPlatformStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", 25);
            stats.put("totalAdmins", 2);
            stats.put("totalTeachers", 8);
            stats.put("totalStudents", 15);
            stats.put("totalFilieres", 3);
            stats.put("totalStages", 12);
            
            // stagesByState
            List<Map<String, Object>> stagesByState = new ArrayList<>();
            Map<String, Object> state1 = new HashMap<>();
            state1.put("state", "VALIDE");
            state1.put("count", 7);
            stagesByState.add(state1);
            stats.put("stagesByState", stagesByState);
            
            stats.put("stagesByFiliere", new ArrayList<>());
            stats.put("topEntreprises", new ArrayList<>());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Récupère les stages par état
     */
    @GetMapping("/stages/by-state")
    public ResponseEntity<?> getStagesByState() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            Map<String, Object> brouillon = new HashMap<>();
            brouillon.put("state", "BROUILLON");
            brouillon.put("count", 2);
            result.add(brouillon);
            
            Map<String, Object> enAttente = new HashMap<>();
            enAttente.put("state", "EN_ATTENTE_VALIDATION");
            enAttente.put("count", 3);
            result.add(enAttente);
            
            Map<String, Object> valide = new HashMap<>();
            valide.put("state", "VALIDE");
            valide.put("count", 5);
            result.add(valide);
            
            Map<String, Object> refuse = new HashMap<>();
            refuse.put("state", "REFUSE");
            refuse.put("count", 1);
            result.add(refuse);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Récupère les stages par filière
     */
    @GetMapping("/stages/by-filiere")
    public ResponseEntity<?> getStagesByFiliere() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            Map<String, Object> filiere1 = new HashMap<>();
            filiere1.put("filiereId", 1);
            filiere1.put("filiereName", "Génie Informatique");
            filiere1.put("count", 5);
            result.add(filiere1);
            
            Map<String, Object> filiere2 = new HashMap<>();
            filiere2.put("filiereId", 2);
            filiere2.put("filiereName", "Sécurité SI");
            filiere2.put("count", 3);
            result.add(filiere2);
            
            Map<String, Object> filiere3 = new HashMap<>();
            filiere3.put("filiereId", 3);
            filiere3.put("filiereName", "Data Science");
            filiere3.put("count", 2);
            result.add(filiere3);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Récupère les top entreprises
     */
    @GetMapping("/top-enterprises")
    public ResponseEntity<?> getTopEnterprises() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            Map<String, Object> ent1 = new HashMap<>();
            ent1.put("entreprise", "TechCorp");
            ent1.put("count", 4);
            result.add(ent1);
            
            Map<String, Object> ent2 = new HashMap<>();
            ent2.put("entreprise", "CloudSecure");
            ent2.put("count", 3);
            result.add(ent2);
            
            Map<String, Object> ent3 = new HashMap<>();
            ent3.put("entreprise", "DataSoft");
            ent3.put("count", 2);
            result.add(ent3);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Export des statistiques
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportStatistics(@RequestParam(defaultValue = "JSON") String format) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("exportedAt", new Date());
            data.put("format", format);
            data.put("message", "Export des statistiques en format " + format);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Statistics service is running");
    }
}



