package com.example.authentification.dto;

import lombok.Data;

/**
 * DTO pour la création d'un utilisateur
 */
@Data
public class CreateUserRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private Long filiereId;
    private String annee;
}
