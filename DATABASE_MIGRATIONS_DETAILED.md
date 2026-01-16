# 📦 Documentation des migrations Flyway - Gestion des Stages

## 🎯 Qu'est-ce qu'une migration?

Une migration est un script SQL qui crée ou modifie la structure de la base de données. 

**Flyway** gère automatiquement l'exécution de ces scripts dans le bon ordre au démarrage de l'application.

---

## 📋 Les 3 migrations du projet

### 📍 Migration V1: Créer la table USERS

**Fichier:** `src/main/resources/db/migration/V1__Create_Users_Table.sql`

**Objectif:** Créer la table qui stocke tous les utilisateurs

**Script SQL:**
```sql
-- Migration V1: Create Users table
-- Date: 2026-01-12
-- Description: Create the users table with authentication fields

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'ENSEIGNANT', 'ETUDIANT') NOT NULL DEFAULT 'ETUDIANT',
    filiere_id BIGINT,
    year_level VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Explications:**
- `IF NOT EXISTS` : Ne crée que si la table n'existe pas
- `BIGINT AUTO_INCREMENT` : ID auto-généré
- `VARCHAR(255) UNIQUE` : Email unique
- `ENUM('ADMIN', 'ENSEIGNANT', 'ETUDIANT')` : 3 rôles possibles
- `INDEX idx_email` : Optimise les recherches par email
- `utf8mb4_unicode_ci` : Support complet Unicode

**Données créées:**
```
Aucune donnée (table vide)
Les données seront ajoutées par l'API
```

---

### 📍 Migration V2: Créer la table FILIERES

**Fichier:** `src/main/resources/db/migration/V2__Create_Filieres_Table.sql`

**Objectif:** Créer la table des programmes/filières

**Script SQL:**
```sql
-- Migration V2: Create Filieres table
-- Date: 2026-01-12
-- Description: Create the filieres (programs) table

CREATE TABLE IF NOT EXISTS filieres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    niveau ENUM('M1', 'M2') NOT NULL,
    description LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_niveau (niveau)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Explications:**
- `LONGTEXT` : Permet des descriptions longues
- `ENUM('M1', 'M2')` : Seulement 2 niveaux possibles
- `INDEX idx_niveau` : Optimise les filtres par niveau
- Pas de clé étrangère (pas de dépendance)

**Données créées:**
```
Aucune donnée (table vide)
Les filières seront créées par les admins via l'API
```

---

### 📍 Migration V3: Créer la table STAGES

**Fichier:** `src/main/resources/db/migration/V3__Create_Stages_Table.sql`

**Objectif:** Créer la table des stages avec les relations et contraintes

**Script SQL:**
```sql
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
    etat ENUM('BROUILLON', 'EN_ATTENTE_VALIDATION', 'VALIDE', 'REFUSE') 
        NOT NULL DEFAULT 'BROUILLON',
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
    CONSTRAINT fk_stages_etudiant 
        FOREIGN KEY (etudiant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_stages_encadrant 
        FOREIGN KEY (encadrant_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_stages_filiere 
        FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Explications:**
- `ON DELETE CASCADE` : Supprimer les stages si l'étudiant est supprimé
- `ON DELETE SET NULL` : Mettre à NULL l'encadrant si supprimé
- `ENUM('BROUILLON', ...)` : 4 états possibles
- 5 index pour optimiser les recherches

**Données créées:**
```
Aucune donnée (table vide)
Les stages seront créés par les étudiants via l'API
```

---

## 📊 Ordre d'exécution des migrations

```
Application démarre
    ↓
Flyway lit les fichiers de migration
    ↓
Exécute V1__Create_Users_Table.sql
    ├─ Crée la table users
    ├─ Ajoute les index
    └─ ✓ V1 terminée
    ↓
Exécute V2__Create_Filieres_Table.sql
    ├─ Crée la table filieres
    ├─ Ajoute les index
    └─ ✓ V2 terminée
    ↓
Exécute V3__Create_Stages_Table.sql
    ├─ Crée la table stages
    ├─ Ajoute les index
    ├─ Ajoute les clés étrangères
    └─ ✓ V3 terminée
    ↓
Enregistre dans flyway_schema_history
    ├─ V1 | 2026-01-12 | CREATE_USERS_TABLE | ✓
    ├─ V2 | 2026-01-12 | CREATE_FILIERES_TABLE | ✓
    └─ V3 | 2026-01-12 | CREATE_STAGES_TABLE | ✓
    ↓
Application prête! 🚀
```

---

## 🔍 Vérifier les migrations exécutées

### Dans MySQL

```sql
-- Voir toutes les migrations exécutées
SELECT * FROM flyway_schema_history;

-- Résultat attendu:
-- installed_rank | version | description              | type | script                          | checksum    | installed_by | installed_on | execution_time | success
-- 1               | 1       | Create Users Table       | SQL  | V1__Create_Users_Table.sql      | 1234567890  | root         | 2026-01-12   | 245            | 1
-- 2               | 2       | Create Filieres Table    | SQL  | V2__Create_Filieres_Table.sql   | 1234567891  | root         | 2026-01-12   | 156            | 1
-- 3               | 3       | Create Stages Table      | SQL  | V3__Create_Stages_Table.sql     | 1234567892  | root         | 2026-01-12   | 423            | 1
```

### Dans les logs Spring Boot

```
INFO ... Flyway : Successfully validated 3 migrations
INFO ... Flyway : Creating Schema History table: flyway_schema_history
INFO ... Flyway : Current version of schema: < 1
INFO ... Flyway : Migrating schema to version 1 - Create Users Table
INFO ... Flyway : Successfully applied 1 migration to schema (execution time: 245ms)
INFO ... Flyway : Migrating schema to version 2 - Create Filieres Table
INFO ... Flyway : Successfully applied 1 migration to schema (execution time: 156ms)
INFO ... Flyway : Migrating schema to version 3 - Create Stages Table
INFO ... Flyway : Successfully applied 1 migration to schema (execution time: 423ms)
INFO ... Flyway : Flyway (v8.x.x) has been successfully applied to schema "authdb"
```

---

## 🚀 Ajouter une nouvelle migration

Si vous voulez ajouter une nouvelle migration à l'avenir:

### Étape 1: Créer le fichier
```
Nom: V4__Add_Description_To_Filieres.sql
Localisation: src/main/resources/db/migration/
```

### Étape 2: Écrire le script SQL
```sql
-- Migration V4: Add description to filieres
-- Date: 2026-02-15
-- Description: Add a longer description field to filieres

ALTER TABLE filieres 
ADD COLUMN long_description LONGTEXT AFTER description;

-- Ajouter une valeur par défaut
UPDATE filieres SET long_description = description WHERE long_description IS NULL;
```

### Étape 3: Redémarrer l'application
```bash
mvn spring-boot:run
```

**Flyway exécutera automatiquement V4!**

---

## ⚠️ Règles importantes pour les migrations

### ✅ À faire

1. **Nommer correctement**: `V{numéro}__{description}.sql`
2. **Numéroter séquentiellement**: V1, V2, V3... (pas de sauts)
3. **Idempotent**: Utiliser `IF NOT EXISTS`
4. **Immutable**: Une fois exécutée, JAMAIS modifier
5. **Commenter**: Ajouter des commentaires expliquant les changements

### ❌ À éviter

1. **Ne pas renommer** une migration existante
2. **Ne pas modifier** une migration déjà exécutée
3. **Ne pas sauter** de numéro de version
4. **Ne pas supprimer** une migration
5. **Ne pas exécuter** deux migrations avec le même numéro

---

## 📈 État du schéma après les migrations

### Tables créées

```
users (Utilisateurs)
  ├─ 11 colonnes
  ├─ 2 index
  └─ Clés étrangères: none

filieres (Programmes)
  ├─ 5 colonnes
  ├─ 1 index
  └─ Clés étrangères: none

stages (Internships)
  ├─ 17 colonnes
  ├─ 5 index
  └─ Clés étrangères: 3 (users x2, filieres x1)
```

### Nombre total de lignes

```
users:    0 (à remplir)
filieres: 0 (à remplir)
stages:   0 (à remplir)
```

---

## 🔄 Réversibilité

**Question:** "Et si une migration échoue?"

**Réponse:** Flyway supportent les migrations de rollback:

### Migration de rollback (V4__Undo_Something.sql)

```sql
-- Migration V4: Undo - Remove description from filieres
-- Date: 2026-02-20
-- Description: Rollback - Remove the column added in V3

ALTER TABLE filieres 
DROP COLUMN long_description;
```

---

## ✅ Checklist des migrations

- [x] V1 crée `users` avec 3 rôles
- [x] V2 crée `filieres` avec M1/M2
- [x] V3 crée `stages` avec workflow
- [x] Flyway configuré dans `application.properties`
- [x] Migrations validées à démarrage
- [x] Historique enregistré dans `flyway_schema_history`
- [x] Index optimisés
- [x] Contraintes d'intégrité en place

---

## 📊 Vue d'ensemble des migrations

| Version | Nom | Tables | Colonnes | Index | Contraintes |
|---------|-----|--------|----------|-------|-------------|
| V1 | Create Users | 1 | 11 | 2 | 0 |
| V2 | Create Filieres | 1 | 5 | 1 | 0 |
| V3 | Create Stages | 1 | 17 | 5 | 3 |
| **Total** | **3 migrations** | **3** | **33** | **8** | **3** |

---

## 🎯 Commandes utiles

### Vérifier le statut des migrations
```bash
# Via logs au démarrage
mvn spring-boot:run | grep -i flyway
```

### Voir l'historique
```sql
mysql> SELECT version, description, success FROM flyway_schema_history;
```

### Forcer une réinitialisation (⚠️ ATTENTION!)
```sql
-- Supprimer l'historique des migrations
DELETE FROM flyway_schema_history;

-- Recommencer à zéro
-- Les tables ne seront pas supprimées
```

---

## 🎉 Résumé

**Vous avez:**
- ✅ 3 migrations SQL prêtes
- ✅ Flyway configuré et fonctionnel
- ✅ Historique automatiquement géré
- ✅ Intégrité des données garantie
- ✅ Scalabilité future pour nouvelles migrations

**Status:** ✅ **Migrations ready for production!**

