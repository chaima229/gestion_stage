package com.example.stage.controller;

import com.example.stage.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(statsService.getSummary());
    }

    @GetMapping("/by-etat")
    public ResponseEntity<Map<String, Long>> getCountByEtat() {
        return ResponseEntity.ok(statsService.getCountByEtat());
    }

    @GetMapping("/by-filiere")
    public ResponseEntity<Map<String, Long>> getCountByFiliere() {
        return ResponseEntity.ok(statsService.getCountByFiliere());
    }
}
