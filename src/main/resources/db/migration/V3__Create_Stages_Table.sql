-- Migration V3: Create Stages table
-- Date: 2026-01-12
-- Description: Create the stages (internships) table with workflow support

CREATE TABLE IF NOT EXISTS stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sujet VARCHAR(255) NOT NULL,
    description LONGTEXT,
    entreprise VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    etat ENUM('BROUILLON', 'EN_ATTENTE_VALIDATION', 'VALIDE', 'REFUSE') NOT NULL DEFAULT 'BROUILLON',
    etudiant_id BIGINT NOT NULL,
    encadrant_id BIGINT,
    filiere_id BIGINT NOT NULL,
    commentaire LONGTEXT,
    rapport_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_etudiant_id (etudiant_id),
    INDEX idx_encadrant_id (encadrant_id),
    INDEX idx_filiere_id (filiere_id),
    INDEX idx_etat (etat),
    INDEX idx_entreprise (entreprise),
    CONSTRAINT fk_stages_etudiant FOREIGN KEY (etudiant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_stages_encadrant FOREIGN KEY (encadrant_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_stages_filiere FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

