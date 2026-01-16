package com.example.stage.entity;

public enum StageEtat {
    BROUILLON,              // Stage créé mais pas encore soumis
    EN_ATTENTE_VALIDATION,  // Soumis et en attente de validation par encadrant
    VALIDE,                 // Validé par l'encadrant
    REFUSE,                 // Refusé par l'encadrant
    EN_COURS,               // Stage en cours d'exécution
    TERMINE,                // Stage terminé
    SOUTENU                 // Stage soutenu avec succès
}
