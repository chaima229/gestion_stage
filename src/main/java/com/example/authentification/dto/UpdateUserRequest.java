package com.example.authentification.dto;

import lombok.Data;

/**
 * DTO pour la mise à jour d'un utilisateur
 */
@Data
public class UpdateUserRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private Long filiereId;
    private String annee;
}
