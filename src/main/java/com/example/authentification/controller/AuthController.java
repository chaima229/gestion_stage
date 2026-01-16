package com.example.authentification.controller;

import com.example.authentification.dto.AuthResponse;
import com.example.authentification.dto.LoginRequest;
import com.example.authentification.dto.RegisterRequest;
import com.example.authentification.entity.User;
import com.example.authentification.repository.UserRepository;
import com.example.authentification.service.AuthService;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint pour l'inscription
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        // enrichir avec infos user
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        user.ifPresent(u -> {
            response.setId(u.getId());
            response.setEmail(u.getEmail());
            response.setRole(u.getRole().name());
            response.setNom(u.getNom());
            response.setPrenom(u.getPrenom());
        });
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour la connexion
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        user.ifPresent(u -> {
            response.setId(u.getId());
            response.setEmail(u.getEmail());
            response.setRole(u.getRole().name());
            response.setNom(u.getNom());
            response.setPrenom(u.getPrenom());
        });
        return ResponseEntity.ok(response);
    }
}
