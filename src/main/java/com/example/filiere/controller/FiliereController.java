package com.example.filiere.controller;

import com.example.filiere.service.FiliereService;
import com.example.authentification.dto.FiliereDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filieres")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class FiliereController {

    private final FiliereService filiereService;

    public FiliereController(FiliereService filiereService) {
        this.filiereService = filiereService;
    }

    @PostMapping
    public ResponseEntity<FiliereDTO> createFiliere(@Valid @RequestBody FiliereDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filiereService.createFiliere(dto));
    }

    @GetMapping
    public ResponseEntity<List<FiliereDTO>> getAllFilieres() {
        return ResponseEntity.ok(filiereService.getAllFilieres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FiliereDTO> getFiliere(@PathVariable Long id) {
        return ResponseEntity.ok(filiereService.getFiliere(id));
    }

    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<List<FiliereDTO>> getFilieresByNiveau(@PathVariable String niveau) {
        return ResponseEntity.ok(filiereService.getFilieresByNiveau(niveau));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FiliereDTO> updateFiliere(@PathVariable Long id, @Valid @RequestBody FiliereDTO dto) {
        return ResponseEntity.ok(filiereService.updateFiliere(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiliere(@PathVariable Long id) {
        filiereService.deleteFiliere(id);
        return ResponseEntity.noContent().build();
    }
}

