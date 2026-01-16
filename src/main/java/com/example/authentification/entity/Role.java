package com.example.authentification.entity;

/**
 * Rôles disponibles dans le système
 * - ADMIN: Administrateur principal avec accès complet
 * - SOUS_ADMIN: Gère les comptes (import Excel, gestion utilisateurs)
 * - ENSEIGNANT: Valide les stages et suit les étudiants
 * - ETUDIANT: Propose et gère ses stages
 */
public enum Role {
    ADMIN,           // Admin principal
    SOUS_ADMIN,      // Gestion des comptes
    ENSEIGNANT,      // Encadrant
    ETUDIANT         // Étudiant
}

