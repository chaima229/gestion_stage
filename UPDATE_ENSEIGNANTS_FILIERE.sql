-- =======================================================================
-- Script de mise à jour : Ajouter des filières aux enseignants
-- Date : 2026-01-15
-- Description : Permet d'assigner une filière aux enseignants existants
-- =======================================================================

-- 1. Voir tous les enseignants sans filière
SELECT id, nom, prenom, email, role, filiereId 
FROM users 
WHERE role = 'ENSEIGNANT' 
ORDER BY nom, prenom;

-- 2. Voir toutes les filières disponibles
SELECT id, nom, description 
FROM filieres 
ORDER BY nom;

-- =======================================================================
-- EXEMPLES D'ASSIGNATION DE FILIÈRE
-- =======================================================================

-- Exemple 1 : Assigner la filière 1 à un enseignant spécifique
-- UPDATE users 
-- SET filiereId = 1 
-- WHERE id = 10 AND role = 'ENSEIGNANT';

-- Exemple 2 : Assigner la filière 1 à tous les enseignants sans filière
-- UPDATE users 
-- SET filiereId = 1 
-- WHERE role = 'ENSEIGNANT' AND filiereId IS NULL;

-- Exemple 3 : Assigner différentes filières selon le nom de l'enseignant
-- UPDATE users SET filiereId = 1 WHERE email = 'enseignant1@example.com';
-- UPDATE users SET filiereId = 2 WHERE email = 'enseignant2@example.com';

-- =======================================================================
-- VÉRIFICATION
-- =======================================================================

-- Voir les enseignants avec leur filière
SELECT 
    u.id,
    u.nom,
    u.prenom,
    u.email,
    u.role,
    u.filiereId,
    f.nom as filiere_nom
FROM users u
LEFT JOIN filieres f ON u.filiereId = f.id
WHERE u.role = 'ENSEIGNANT'
ORDER BY u.nom, u.prenom;

-- Compter les enseignants par filière
SELECT 
    f.nom as filiere,
    COUNT(u.id) as nombre_enseignants
FROM filieres f
LEFT JOIN users u ON f.id = u.filiereId AND u.role = 'ENSEIGNANT'
GROUP BY f.id, f.nom
ORDER BY f.nom;
